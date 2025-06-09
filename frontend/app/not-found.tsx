'use client'

import { Home, ArrowLeft, Search } from 'lucide-react'
import { useRouter } from 'next/navigation'
import { Button } from '@/_shared/ui/button'

export default function NotFound() {
  const router = useRouter()

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4">
      <div className="max-w-md w-full text-center space-y-8">
        {/* Animated 404 Text */}
        <div className="relative">
          <h1 className="text-[150px] font-bold text-transparent bg-clip-text bg-gradient-to-r from-primary/50 to-primary animate-pulse">
            404
          </h1>
          <div className="absolute inset-0 blur-3xl bg-primary/20 animate-pulse" />
        </div>

        {/* Error Message */}
        <div className="space-y-3">
          <h2 className="text-2xl font-semibold text-text-primary">Page Not Found</h2>
          <p className="text-text-secondary">
            The page you're looking for doesn't exist or has been moved.
          </p>
        </div>

        {/* Navigation Buttons */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center pt-4">
          <Button variant="default" className="gap-2" onClick={() => router.push('/')}>
            <Home className="h-4 w-4" />
            <span>Back to Home</span>
          </Button>

          <Button variant="outline" className="gap-2" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4" />
            <span>Go Back</span>
          </Button>

          <Button variant="ghost" className="gap-2" onClick={() => router.push('/feed')}>
            <Search className="h-4 w-4" />
            <span>Browse Feed</span>
          </Button>
        </div>

        {/* Decorative Elements */}
        <div className="absolute inset-0 -z-10 h-full w-full bg-background bg-[linear-gradient(to_right,#8080800a_1px,transparent_1px),linear-gradient(to_bottom,#8080800a_1px,transparent_1px)] bg-[size:24px_24px]"></div>
      </div>
    </div>
  )
}
