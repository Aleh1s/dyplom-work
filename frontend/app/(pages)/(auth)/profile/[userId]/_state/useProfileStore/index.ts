import { toast } from 'sonner'
import { create } from 'zustand'
import { postsApi } from '@/_shared/api/posts'
import { subscriptionsApi } from '@/_shared/api/subscriptions'
import type { Post, SubscriptionPlan, UserProfile, UserType } from '@/_shared/api/types'
import { usersApi } from '@/_shared/api/users'

interface ProfileStoreType {
  userProfile: UserProfile | null
  posts: Post[]
  subscriptionPlan: SubscriptionPlan | null
  totalSubscribers: number
  isUserProfileLoading: boolean
  isSubscriptionPlanLoading: boolean
  isPostsLoading: boolean
  fetchUserProfile: (userId: string) => Promise<void>
  fetchSubscriptionPlan: (userId: string) => Promise<void>
  fetchPosts: (userId: string) => Promise<void>
  likePost: (postId: string) => Promise<void>
  unlikePost: (postId: string) => Promise<void>
  commentPost: (postId: string, comment: string) => Promise<void>
  updateUserProfile: (userProfile: Partial<UserType>) => Promise<void>
  subscribe: (userId: string) => Promise<void>
  unsubscribe: (userId: string) => Promise<void>
  fetchTotalSubscribers: (userId: string) => Promise<void>
}

const useProfileStore = create<ProfileStoreType>((set, get) => ({
  userProfile: null,
  posts: [],
  subscriptionPlan: null,
  totalSubscribers: 0,
  isUserProfileLoading: false,
  isSubscriptionPlanLoading: false,
  isPostsLoading: false,
  fetchUserProfile: async (userId: string) => {
    const { isUserProfileLoading } = get()

    if (isUserProfileLoading) return

    try {
      set({ isUserProfileLoading: true })

      const response = await usersApi.getUserProfileById(userId)

      set({ userProfile: response })
    } catch {
      toast.error('Failed to fetch user profile')
    } finally {
      set({ isUserProfileLoading: false })
    }
  },
  fetchSubscriptionPlan: async (userId: string) => {
    const { isSubscriptionPlanLoading } = get()

    if (isSubscriptionPlanLoading) return

    try {
      set({ isSubscriptionPlanLoading: true })

      const response = await subscriptionsApi.getPlan(userId)

      set({ subscriptionPlan: response })
    } catch {
      toast.error('Failed to fetch subscription plan')
    } finally {
      set({ isSubscriptionPlanLoading: false })
    }
  },
  fetchPosts: async (userId: string) => {
    const { isPostsLoading } = get()

    if (isPostsLoading) return

    try {
      set({ isPostsLoading: true })

      const response = await postsApi.getPostsByUserId(userId)

      set({ posts: response })
    } catch {
      toast.error('Failed to fetch posts')
    } finally {
      set({ isPostsLoading: false })
    }
  },
  likePost: async (postId: string) => {
    try {
      await postsApi.likePost({ postId })

      set(state => ({
        posts: state.posts.map(post =>
          post.id === postId
            ? { ...post, hasUserLike: true, likesCount: post.likesCount + 1 }
            : post
        )
      }))
    } catch {
      toast.error('Failed to like post')
    }
  },
  unlikePost: async (postId: string) => {
    try {
      await postsApi.unlikePost({ postId })

      set(state => ({
        posts: state.posts.map(post =>
          post.id === postId
            ? { ...post, hasUserLike: false, likesCount: post.likesCount - 1 }
            : post
        )
      }))
    } catch {
      toast.error('Failed to unlike post')
    }
  },
  commentPost: async (postId: string, comment: string) => {
    if (!comment) return

    try {
      const newComment = await postsApi.commentPost({ postId, comment })

      set(state => ({
        posts: state.posts.map(post =>
          post.id === postId
            ? {
                ...post,
                comments: [...post.comments, newComment],
                commentsCount: post.commentsCount + 1
              }
            : post
        )
      }))
    } catch {
      toast.error('Failed to comment on post')
    }
  },
  updateUserProfile: async (userProfile: Partial<UserType>) => {
    set(state => {
      if (!state.userProfile) return { userProfile: null }

      return {
        userProfile: {
          ...state.userProfile,
          ...userProfile,
          subscribedOn: state.userProfile.subscribedOn
        }
      }
    })
  },
  subscribe: async (userId: string) => {
    try {
      const response = await subscriptionsApi.subscribe(userId)

      set(state => {
        if (!state.userProfile) return { userProfile: null }

        return {
          userProfile: {
            ...state.userProfile,
            subscribedOn: response
          }
        }
      })

      get().fetchPosts(userId)
      get().fetchTotalSubscribers(userId)
    } catch {
      toast.error('Failed to subscribe')
    }
  },
  unsubscribe: async (userId: string) => {
    try {
      const response = await subscriptionsApi.unsubscribe(userId)

      set(state => {
        if (!state.userProfile) return { userProfile: null }

        return {
          userProfile: {
            ...state.userProfile,
            subscribedOn: response
          }
        }
      })
    } catch {
      toast.error('Failed to unsubscribe')
    }
  },
  fetchTotalSubscribers: async (userId: string) => {
    try {
      const response = await subscriptionsApi.getTotalSubscribers(userId)

      set({ totalSubscribers: response.totalSubscribers })
    } catch {
      toast.error('Failed to fetch total subscribers')
    }
  }
}))

export default useProfileStore
