import api from '../config'
import type { UserProfile, UserType } from '../types'

export const usersApi = {
  me: () => {
    return api.post('users/me').json<UserType>()
  },
  updateMe: (user: Partial<UserType>) => {
    return api
      .put('users/me', {
        json: user
      })
      .json<UserType>()
  },
  getUserProfileById: (userId: string) => {
    return api.get(`users/${userId}`).json<UserProfile>()
  },
  searchUsers: (usernameLike: string) => {
    return api.get(`users/search?username=${usernameLike}`).json<UserType[]>()
  }
}
