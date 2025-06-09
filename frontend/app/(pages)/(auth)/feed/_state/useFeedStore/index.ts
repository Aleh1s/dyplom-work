import { toast } from 'sonner'
import { create } from 'zustand'
import type { CreatePostRequest } from '@/_shared/api/posts'
import { postsApi } from '@/_shared/api/posts'
import type { NewPost, Post, PostTypeType } from '@/_shared/api/types'

interface FeedStoreType {
  posts: Post[]
  page: number
  hasMore: boolean
  selectedFilter: PostTypeType | 'ALL'
  isLoading: boolean
  error: string | null
  fetchPosts: () => Promise<void>
  setFilter: (filter: PostTypeType | 'ALL') => Promise<void>
  resetFeed: () => void
  likePost: (postId: string) => Promise<void>
  unlikePost: (postId: string) => Promise<void>
  commentPost: (postId: string, comment: string) => Promise<void>
  createPost: (post: CreatePostRequest) => Promise<NewPost | undefined>
}

const POSTS_PER_PAGE = 10

const useFeedStore = create<FeedStoreType>((set, get) => ({
  posts: [],
  page: 0,
  hasMore: true,
  selectedFilter: 'ALL',
  isLoading: false,
  error: null,

  fetchPosts: async () => {
    const { page, selectedFilter, isLoading } = get()

    if (isLoading) return

    try {
      set({ isLoading: true, error: null })

      const response = await postsApi.getPostsFeed({
        page,
        size: POSTS_PER_PAGE,
        postType: selectedFilter === 'ALL' ? undefined : selectedFilter
      })

      set(state => ({
        posts: [...state.posts, ...response.content],
        page: state.page + 1,
        hasMore: !response.last,
        isLoading: false
      }))
    } catch {
      set({
        error: 'Failed to fetch posts',
        isLoading: false
      })
    }
  },
  setFilter: async (filter: PostTypeType | 'ALL') => {
    set({
      selectedFilter: filter,
      posts: [],
      page: 0,
      hasMore: true,
      error: null
    })

    await get().fetchPosts()
  },
  resetFeed: () => {
    set({
      posts: [],
      page: 0,
      hasMore: true,
      error: null
    })
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
  createPost: async (post: CreatePostRequest) => {
    return await postsApi.createPost(post)
  }
}))

export default useFeedStore
