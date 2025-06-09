import api from '../config'
import type { MediaUrl, SafeSaveMediaResponse } from '../types'

export const mediaApi = {
  upload: (file: File) => {
    const formData = new FormData()

    formData.append('file', file)

    return api
      .post('media/upload', {
        body: formData
      })
      .json<MediaUrl>()
  },
  uploadSafe: (file: File) => {
    const formData = new FormData()

    formData.append('file', file)

    return api.post('media/safe/upload', { body: formData }).json<SafeSaveMediaResponse>()
  }
}
