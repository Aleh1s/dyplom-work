'use client'

import React, { useCallback, useEffect, useState } from 'react'
import {
  LineChart,
  Line,
  ResponsiveContainer,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts'
import { toast } from 'sonner'
import MainLayout from '@/_components/layout/MainLayout'
import { statisticsApi } from '@/_shared/api/statistics'
import type { UserAnalytics } from '@/_shared/api/types'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Calendar } from '@/_shared/ui/calendar'
import { Card } from '@/_shared/ui/card'

const Analytics = () => {
  const user = useAuthStore(state => state.user)

  const [date, setDate] = useState<[Date | undefined, Date | undefined]>([
    new Date(new Date().setDate(new Date().getDate() - 7)),
    new Date()
  ])

  const [analytics, setAnalytics] = useState<UserAnalytics | null>(null)

  const fetchAnalytics = useCallback(async () => {
    if (!user) return

    try {
      const analytics = await statisticsApi.getUserAnalytics(
        user.id,
        date[0]?.toISOString() ?? '',
        date[1]?.toISOString() ?? ''
      )

      setAnalytics(analytics)
    } catch {
      toast.error('Failed to fetch analytics')
    }
  }, [date, user])

  useEffect(() => {
    fetchAnalytics()
  }, [date, fetchAnalytics])

  const revenueData = [
    { date: '2024-01', revenue: 2400 },
    { date: '2024-02', revenue: 1398 },
    { date: '2024-03', revenue: 9800 },
    { date: '2024-04', revenue: 3908 },
    { date: '2024-05', revenue: 4800 },
    { date: '2024-06', revenue: 3800 }
  ]

  const subscriberData =
    analytics?.subscriberGrowth.map(item => ({
      date: item.key,
      subscribers: item.value
    })) ?? []

  const likesData =
    analytics?.likesGrowth.map(item => ({
      date: item.key,
      likes: item.value
    })) ?? []

  const commentsData =
    analytics?.commentsGrowth.map(item => ({
      date: item.key,
      comments: item.value
    })) ?? []

  return (
    <MainLayout>
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-bold">Analytics</h1>
          <p className="text-muted-foreground">
            Track your content performance and audience engagement.
          </p>
        </div>

        <div className="space-y-6">
          <Card className="p-6 bg-background-surface border-white/10">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-medium">Revenue Overview</h2>
              <div>
                <Calendar
                  mode="range"
                  selected={{ from: date[0], to: date[1] }}
                  onSelect={range => {
                    if (range?.from && range?.to) {
                      setDate([range.from, range.to])
                    }
                  }}
                  numberOfMonths={2}
                  initialFocus
                />
              </div>
            </div>
            <div className="h-[350px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={revenueData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="revenue" stroke="#8884d8" activeDot={{ r: 8 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
            <div className="text-sm text-muted-foreground">
              Revenue from {date[0]?.toLocaleDateString()} to {date[1]?.toLocaleDateString()}
            </div>
          </Card>

          <Card className="p-6 bg-background-surface border-white/10">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-medium">Subscriber Growth</h2>
            </div>
            <div className="h-[350px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart
                  data={subscriberData}
                  margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="subscribers"
                    stroke="#82ca9d"
                    activeDot={{ r: 8 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
            <div className="text-sm text-muted-foreground">New subscribers over time.</div>
          </Card>

          <Card className="p-6 bg-background-surface border-white/10">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-medium">Likes</h2>
            </div>
            <div className="h-[350px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={likesData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="likes" stroke="#e48900" activeDot={{ r: 8 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
            <div className="text-sm text-muted-foreground">Likes per content piece.</div>
          </Card>

          <Card className="p-6 bg-background-surface border-white/10">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-medium">Comments</h2>
            </div>
            <div className="h-[350px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={commentsData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="comments" stroke="#e48900" activeDot={{ r: 8 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </div>
      </div>
    </MainLayout>
  )
}

export default Analytics
