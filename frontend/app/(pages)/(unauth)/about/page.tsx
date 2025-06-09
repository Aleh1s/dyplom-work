import React from 'react'
import MainLayout from '@/_components/layout/MainLayout'
import { Card } from '@/_shared/ui/card'

const About = () => {
  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-8">About Our Platform</h1>

        <div className="space-y-6">
          <Card className="p-6 bg-background-surface border-white/10">
            <h2 className="text-xl font-semibold mb-4">Welcome to Our Creator Platform</h2>
            <p className="text-muted-foreground mb-4">
              We provide a space for creators to share and monetize their content while building
              meaningful connections with their audience.
            </p>
          </Card>

          <Card className="p-6 bg-background-surface border-white/10">
            <h2 className="text-xl font-semibold mb-4">Our Features</h2>
            <ul className="space-y-3 text-muted-foreground">
              <li>• Subscription-based content sharing</li>
              <li>• Pay-per-content options</li>
              <li>• Analytics and insights</li>
              <li>• Direct creator-subscriber communication</li>
            </ul>
          </Card>
        </div>
      </div>
    </MainLayout>
  )
}

export default About
