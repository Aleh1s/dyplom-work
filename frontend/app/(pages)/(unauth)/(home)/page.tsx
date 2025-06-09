'use client'

import Link from 'next/link'
import { signIn } from 'next-auth/react'
import React from 'react'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Button } from '@/_shared/ui/button'

const Index = () => {
  const user = useAuthStore(state => state.user)

  return (
    <div className="min-h-screen bg-background flex flex-col">
      {/* Header/Navigation */}
      <header className="border-b border-white/10 backdrop-blur-md bg-background/80 sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <Link href="/" className="text-2xl font-bold text-gradient">
            ReShare
          </Link>

          <div className="flex items-center gap-3">
            {user ? (
              <Button asChild>
                <Link href="/dashboard">Dashboard</Link>
              </Button>
            ) : (
              <>
                <Button variant="outline" onClick={() => signIn('keycloak')}>
                  Sign In
                </Button>
              </>
            )}
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative overflow-hidden py-20 md:py-32">
        <div className="container mx-auto px-4 relative z-10">
          <div className="max-w-3xl mx-auto text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6">
              <span className="text-gradient">Monetize</span> Your Content, <br />
              <span className="text-gradient">Amplify</span> Your Creativity
            </h1>
            <p className="text-lg md:text-xl text-text-secondary mb-8 max-w-2xl mx-auto">
              The all-in-one platform for creators to share, sell, and grow their digital content
              business with powerful subscription tools and analytics.
            </p>
            <div className="flex flex-wrap justify-center gap-4">
              <Button size="lg" className="bg-premium-gradient hover:opacity-90 px-8" asChild>
                <Link href={user ? '/dashboard' : '/signup'}>
                  {user ? 'Go to Dashboard' : 'Start Creating'}
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="px-8" asChild>
                <Link href="/about">Learn More</Link>
              </Button>
            </div>
          </div>
        </div>

        {/* Background gradient effects */}
        <div className="absolute -top-24 -left-24 w-96 h-96 bg-primary/20 rounded-full blur-3xl opacity-20"></div>
        <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-secondary/20 rounded-full blur-3xl opacity-20"></div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-background-surface">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">Everything You Need to Succeed</h2>
            <p className="text-text-secondary max-w-2xl mx-auto">
              Our platform provides all the tools and features you need to build a successful
              creator business
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="bg-background p-8 rounded-2xl border border-white/10">
              <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-6">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6 text-primary"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <h3 className="text-xl font-bold mb-3">Flexible Monetization</h3>
              <p className="text-text-secondary">
                Choose between subscription models, one-time purchases, or a combination to maximize
                your revenue streams.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="bg-background p-8 rounded-2xl border border-white/10">
              <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-6">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6 text-primary"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                  />
                </svg>
              </div>
              <h3 className="text-xl font-bold mb-3">Powerful Analytics</h3>
              <p className="text-text-secondary">
                Track your growth, understand your audience, and make data-driven decisions with our
                comprehensive analytics dashboard.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="bg-background p-8 rounded-2xl border border-white/10">
              <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-6">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6 text-primary"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                  />
                </svg>
              </div>
              <h3 className="text-xl font-bold mb-3">Content Protection</h3>
              <p className="text-text-secondary">
                Keep your premium content secure with our built-in protection features and paywall
                management.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20">
        <div className="container mx-auto px-4">
          <div className="max-w-4xl mx-auto bg-background-surface p-8 md:p-12 rounded-2xl border border-white/10 relative overflow-hidden">
            {/* Background effect */}
            <div className="absolute -top-24 -right-24 w-64 h-64 bg-premium-gradient rounded-full blur-3xl opacity-20"></div>

            <div className="relative z-10">
              <h2 className="text-2xl md:text-3xl font-bold mb-4">
                Ready to Start Your Creator Journey?
              </h2>
              <p className="text-text-secondary mb-8 max-w-2xl">
                Join thousands of creators who are building sustainable businesses with our
                platform. Sign up today and start monetizing your content.
              </p>
              <Button size="lg" className="bg-premium-gradient hover:opacity-90" asChild>
                <Link href="/signup">Create Your Account</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="mt-auto py-12 bg-background border-t border-white/10">
        <div className="container mx-auto px-4">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="mb-4 md:mb-0">
              <Link href="/" className="text-xl font-bold text-gradient">
                ReShare
              </Link>
              <p className="text-text-secondary text-sm mt-2">
                The premium platform for content creators
              </p>
            </div>

            <div className="flex flex-wrap gap-6">
              <Link href="/about" className="text-text-secondary hover:text-primary">
                About
              </Link>
              <Link href="/features" className="text-text-secondary hover:text-primary">
                Features
              </Link>
              <Link href="/pricing" className="text-text-secondary hover:text-primary">
                Pricing
              </Link>
              <Link href="/help" className="text-text-secondary hover:text-primary">
                Help
              </Link>
              <Link href="/terms" className="text-text-secondary hover:text-primary">
                Terms
              </Link>
              <Link href="/privacy" className="text-text-secondary hover:text-primary">
                Privacy
              </Link>
            </div>
          </div>
          <div className="text-center text-text-secondary text-sm mt-8">
            &copy; {new Date().getFullYear()} ReShare. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  )
}

export default Index
