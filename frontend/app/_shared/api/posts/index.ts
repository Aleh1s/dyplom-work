import api from '../config'
import type { Comment, NewPost, Pageable, Post, PostTypeType, PostWithCounts } from '../types'

interface GetPostsFeedParams {
  page?: number
  size?: number
  postType?: PostTypeType
}

export interface CreatePostRequest {
  description: string
  contentIds: string[]
  type: PostTypeType
}

export const postsApi = {
  getPostsFeed: ({ page, size, postType }: GetPostsFeedParams) => {
    const params = new URLSearchParams({
      page: page?.toString() || '0',
      size: size?.toString() || '10'
    })

    if (postType) {
      params.append('postType', postType)
    }

    return api
      .get('posts/feed', {
        searchParams: params
      })
      .json<Pageable<Post>>()
  },
  likePost: ({ postId }: { postId: string }) => {
    return api.post(`posts/${postId}/like`)
  },
  unlikePost: ({ postId }: { postId: string }) => {
    return api.post(`posts/${postId}/unlike`)
  },
  commentPost: ({ postId, comment }: { postId: string; comment: string }) => {
    return api
      .post(`posts/${postId}/comment`, {
        json: { content: comment }
      })
      .json<Comment>()
  },
  createPost: (request: CreatePostRequest) => {
    return api
      .post('posts', {
        json: request
      })
      .json<NewPost>()
  },
  getPostsByUserId: (userId: string) => {
    return api
      .get('posts', {
        searchParams: {
          ownerId: userId
        }
      })
      .json<Post[]>()
  },
  getLatestPosts: (ownerId: string) => {
    return api
      .get('posts/latest', {
        searchParams: {
          ownerId
        }
      })
      .json<PostWithCounts[]>()
  }
}
