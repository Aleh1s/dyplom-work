'use client'

import { useRef } from 'react'
import type { SubscriptionPlan, UserType } from '@/_shared/api/types'
import useAuthStore from '@/_shared/state/useAuthStore'

interface Props {
  user: UserType | null
  subscriptionPlan: SubscriptionPlan | null
}

const AuthInitializer = ({ user, subscriptionPlan }: Props) => {
  const initialized = useRef(false)

  if (!initialized.current) {
    useAuthStore.setState({ user, subscriptionPlan, isLoading: false })
    initialized.current = true
  }

  return null
}

export default AuthInitializer
