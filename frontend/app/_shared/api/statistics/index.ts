import api from '../config'
import type { UserOverviewStatistics, UserAnalytics } from '../types'

export const statisticsApi = {
  getUserOverviewStatistics: (userId: string) =>
    api.get(`statistics/users/${userId}`).json<UserOverviewStatistics>(),
  getUserAnalytics: (userId: string, from: string, to: string) =>
    api.get(`statistics/users/${userId}/analytics?from=${from}&to=${to}`).json<UserAnalytics>()
}
