'use client'

import { Loader2 } from 'lucide-react'
import Link from 'next/link'
import { redirect } from 'next/navigation'
import useAuthStore from '@/_shared/state/useAuthStore'

const AuthLayout = ({ children }: { children: React.ReactNode }) => {
  const user = useAuthStore(state => state.user)
  const isLoading = useAuthStore(state => state.isLoading)

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen flex-col">
        <Link href="/" className="text-6xl font-bold text-gradient">
          ReShare
        </Link>
        <Loader2 className="h-10 w-10 mt-4 animate-spin" />
      </div>
    )
  }

  if (!user) {
    return redirect('/')
  }

  return children
}

export default AuthLayout
