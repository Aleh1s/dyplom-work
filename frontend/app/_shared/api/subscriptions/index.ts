import api from '../config'
import type { SubscribersInfo, Subscription, SubscriptionPlan } from '../types'

export type UpdateSubscriptionPlan = Pick<SubscriptionPlan, 'type' | 'price'>

export const subscriptionsApi = {
  getPlan: (userId: string) => {
    return api
      .get('subscriptions/plan', {
        searchParams: {
          userId
        }
      })
      .json<SubscriptionPlan>()
  },
  updateSubscriptionPlan: (subscriptionPlan: UpdateSubscriptionPlan) => {
    return api
      .put('subscriptions/plan', {
        json: subscriptionPlan
      })
      .json<SubscriptionPlan>()
  },
  subscribe: (userId: string) => {
    return api
      .post('subscriptions/subscribe', {
        json: {
          subscribeOnId: userId
        }
      })
      .json<Subscription>()
  },
  unsubscribe: (userId: string) => {
    return api
      .post('subscriptions/unsubscribe', {
        json: {
          subscribedOnId: userId
        }
      })
      .json<Subscription>()
  },
  getTotalSubscribers: (userId: string) => {
    return api
      .get('subscriptions/subscribers-info', {
        searchParams: {
          userId
        }
      })
      .json<SubscribersInfo>()
  }
}
