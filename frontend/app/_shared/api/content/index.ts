import api from '../config'
import type { Content } from '../types'

export interface NewContent {
  description: string
  mediaUrl: string
  previewUrl: string
  safePreviewUrl: string
}

export const contentApi = {
  create: (data: NewContent) => {
    return api
      .post('content', {
        json: data
      })
      .json<Content>()
  },
  geyMyGallery: () => {
    return api.get('content/my-gallery').json<Content[]>()
  }
}
