'use client'

import { Users } from 'lucide-react'
import Link from 'next/link'
import React from 'react'
import MainLayout from '@/_components/layout/MainLayout'
import { Avatar, AvatarFallback, AvatarImage } from '@/_shared/ui/avatar'
import { Card } from '@/_shared/ui/card'

const Subscribers = () => {
  const mockSubscribers = [
    { id: 1, name: 'John Doe', subscriptionType: 'Monthly', joinDate: '2024-04-01', avatar: null },
    { id: 2, name: 'Jane Smith', subscriptionType: 'Yearly', joinDate: '2024-03-15', avatar: null }
  ]

  return (
    <MainLayout>
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold">Subscribers</h1>

          <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-background-surface border border-white/10">
            <Users className="w-5 h-5 text-primary" />
            <span className="font-medium">{mockSubscribers.length} Active</span>
          </div>
        </div>

        <div className="grid gap-4">
          {mockSubscribers.map(subscriber => (
            <Link
              key={subscriber.id}
              href={`/subscriber/${subscriber.id}`}
              className="block transition-transform hover:scale-[1.02]"
            >
              <Card className="p-4 bg-background-surface border-white/10">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Avatar>
                      <AvatarImage src={subscriber.avatar || undefined} />
                      <AvatarFallback>{subscriber.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <div>
                      <h3 className="font-medium">{subscriber.name}</h3>
                      <p className="text-sm text-muted-foreground">
                        {subscriber.subscriptionType} Subscription
                      </p>
                    </div>
                  </div>
                  <div className="text-sm text-muted-foreground">Joined {subscriber.joinDate}</div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      </div>
    </MainLayout>
  )
}

export default Subscribers
