import { getServerSession } from 'next-auth'
import type { SubscriptionPlan, UserType } from '@/_shared/api/types'
import { authOptions } from '@/lib/auth'

const getInitialUserData = async () => {
  try {
    const session = await getServerSession(authOptions)

    if (!session?.accessToken) {
      throw new Error('No session found')
    }

    const user = await getUserData(session.accessToken)
    const subscriptionPlan = await getUserSubscription(session.accessToken)

    return {
      user,
      subscriptionPlan
    }
  } catch (error) {
    // Log error on server side instead of using client-side toast
    console.error('Failed to fetch initial user data:', error)

    return {
      user: null,
      subscriptionPlan: null
    }
  }
}

const getUserData = async (token: string) => {
  const response = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/users/me`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`
    }
  })

  if (!response.ok) {
    throw new Error(`Failed to fetch user data: ${response.statusText}`)
  }

  const user = await response.json()

  return user as UserType
}

const getUserSubscription = async (token: string) => {
  const response = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/subscriptions/plan`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`
    }
  })

  if (!response.ok) {
    throw new Error(`Failed to fetch user subscription: ${response.statusText}`)
  }

  const subscriptionPlan = await response.json()

  return subscriptionPlan as SubscriptionPlan
}

export default getInitialUserData
