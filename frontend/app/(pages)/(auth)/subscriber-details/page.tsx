'use client'

import { User } from 'lucide-react'
import React from 'react'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts'
import MainLayout from '@/_components/layout/MainLayout'
import { Card } from '@/_shared/ui/card'

const SubscriberDetails = () => {
  // Mock data for the subscriber
  const subscriberData = {
    name: 'John Doe',
    subscriptionType: 'Monthly',
    joinDate: '2024-04-01',
    totalSpent: 150,
    purchaseHistory: [
      { date: '2024-04', amount: 30 },
      { date: '2024-03', amount: 45 },
      { date: '2024-02', amount: 35 },
      { date: '2024-01', amount: 40 }
    ]
  }

  return (
    <MainLayout>
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center gap-3 mb-8">
          <User className="w-6 h-6" />
          <h1 className="text-2xl font-bold">{subscriberData.name}</h1>
        </div>

        <div className="grid gap-6">
          <Card className="p-6 bg-background-surface border-white/10">
            <h2 className="text-lg font-medium mb-4">Overview</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">Subscription Type</p>
                <p className="font-medium">{subscriberData.subscriptionType}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Join Date</p>
                <p className="font-medium">{subscriberData.joinDate}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Total Spent</p>
                <p className="font-medium">${subscriberData.totalSpent}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6 bg-background-surface border-white/10">
            <h2 className="text-lg font-medium mb-4">Purchase History</h2>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={subscriberData.purchaseHistory}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Line type="monotone" dataKey="amount" stroke="#6366F1" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </div>
      </div>
    </MainLayout>
  )
}

export default SubscriberDetails
