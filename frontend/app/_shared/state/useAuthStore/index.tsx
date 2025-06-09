import { toast } from 'sonner'
import { create } from 'zustand'
import type { UpdateSubscriptionPlan } from '@/_shared/api/subscriptions'
import { subscriptionsApi } from '@/_shared/api/subscriptions'
import type { SubscriptionPlan, UserType } from '@/_shared/api/types'
import { usersApi } from '@/_shared/api/users'

interface AuthStoreType {
  user: UserType | null
  subscriptionPlan: SubscriptionPlan | null
  isLoading: boolean
  fetchMe: () => Promise<void>
  updateMe: (user: Partial<UserType>) => Promise<UserType | undefined>
  updateSubscriptionPlan: (subscriptionPlan: UpdateSubscriptionPlan) => Promise<void>
}

const useAuthStore = create<AuthStoreType>(set => ({
  user: null,
  subscriptionPlan: null,
  isLoading: true,
  fetchMe: async () => {
    try {
      const response = await usersApi.me()

      set({ user: response })
    } catch {
      toast.error('Failed to fetch me')
    }
  },
  updateMe: async (user: Partial<UserType>) => {
    try {
      const response = await usersApi.updateMe(user)

      set({ user: response })

      return response
    } catch {
      toast.error('Failed to update me')
    }
  },
  updateSubscriptionPlan: async (subscriptionPlan: UpdateSubscriptionPlan) => {
    try {
      const response = await subscriptionsApi.updateSubscriptionPlan(subscriptionPlan)

      set({ subscriptionPlan: response })
    } catch {
      toast.error('Failed to update subscription plan')
    }
  }
}))

export default useAuthStore
