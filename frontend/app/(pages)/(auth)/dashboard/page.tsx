'use client'

import {
  TrendingUp,
  Users,
  DollarSign,
  Image,
  MessageCircle,
  Heart,
  Calendar,
  ChevronRight,
  User,
  Loader2,
  TrendingDown
} from 'lucide-react'
import Link from 'next/link'
import { signIn } from 'next-auth/react'
import React, { useCallback, useEffect, useState } from 'react'
import {
  LineChart,
  Line,
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip
} from 'recharts'
import { toast } from 'sonner'
import MainLayout from '@/_components/layout/MainLayout'
import { postsApi } from '@/_shared/api/posts'
import { statisticsApi } from '@/_shared/api/statistics'
import type { PostWithCounts, UserOverviewStatistics } from '@/_shared/api/types'
import { formatDate } from '@/_shared/lib/formatDate'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Button } from '@/_shared/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/_shared/ui/card'

// Mock data for upcoming events
const upcomingEvents = [
  {
    id: '1',
    title: 'Live Q&A Session',
    date: 'Aug 15, 2023',
    time: '6:00 PM'
  },
  {
    id: '2',
    title: 'New Tutorial Release',
    date: 'Aug 20, 2023',
    time: '2:00 PM'
  },
  {
    id: '3',
    title: 'New Tutorial Release',
    date: 'Aug 20, 2023',
    time: '2:00 PM'
  }
]

const Dashboard = () => {
  const user = useAuthStore(state => state.user)

  const [statistics, setStatistics] = useState<UserOverviewStatistics | null>(null)
  const [latestPosts, setLatestPosts] = useState<PostWithCounts[]>([])

  const [isLatestPostsLoading, setIsLatestPostsLoading] = useState(true)
  const [isStatisticsLoading, setIsStatisticsLoading] = useState(true)

  const fetchStatistics = useCallback(async () => {
    if (!user) return

    setIsStatisticsLoading(true)
    try {
      const statistics = await statisticsApi.getUserOverviewStatistics(user.id)

      setStatistics(statistics)
    } catch {
      toast.error('Failed to fetch statistics')
    } finally {
      setIsStatisticsLoading(false)
    }
  }, [user])

  const fetchLatestPosts = useCallback(async () => {
    if (!user) return

    setIsLatestPostsLoading(true)
    try {
      const latestPosts = await postsApi.getLatestPosts(user.id)

      setLatestPosts(latestPosts)
    } catch {
      toast.error('Failed to fetch latest posts')
    } finally {
      setIsLatestPostsLoading(false)
    }
  }, [user])

  useEffect(() => {
    fetchStatistics()
    fetchLatestPosts()
  }, [user, fetchStatistics, fetchLatestPosts])

  if (isStatisticsLoading || isLatestPostsLoading) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <Loader2 className="h-16 w-16 animate-spin" />
        </div>
      </MainLayout>
    )
  }

  if (!user || !statistics) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <div className="text-center">
            <User className="h-16 w-16 mx-auto opacity-20 mb-4" />
            <h2 className="text-xl font-semibold">Not logged in</h2>
            <p className="text-text-secondary mt-2 mb-4">
              You need to sign in to view your dashboard
            </p>
            <Button onClick={() => signIn('keycloak')}>Sign In</Button>
          </div>
        </div>
      </MainLayout>
    )
  }

  const subscribersData = statistics.subscribersCountByMonth.map(item => ({
    name: item.key.substring(0, 3),
    value: item.value
  }))

  const revenueData = statistics.totalRevenueByMonth.map(item => ({
    name: item.key.substring(0, 3),
    value: item.value
  }))

  return (
    <MainLayout>
      <div className="container mx-auto">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold">Welcome back, {user.givenName}</h1>
          <p className="text-text-secondary">Here's an overview of your creator profile</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {/* Revenue Card */}
          <Card className="bg-background-surface border-white/10">
            <CardHeader className="pb-2">
              <CardTitle className="text-md font-medium flex items-center justify-between">
                <span>Total Revenue</span>
                <DollarSign className="h-4 w-4 text-primary" />
              </CardTitle>
              <CardDescription>This month</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${statistics.totalRevenue}</div>
              {statistics.totalRevenueGrowPercent > 0 && (
                <div className="text-xs text-green-500 flex items-center mt-1">
                  <TrendingUp className="h-3 w-3 mr-1" />
                  <span>+{statistics.totalRevenueGrowPercent}% from last month</span>
                </div>
              )}
              {statistics.totalRevenueGrowPercent < 0 && (
                <div className="text-xs text-red-500 flex items-center mt-1">
                  <TrendingDown className="h-3 w-3 mr-1" />
                  <span>{statistics.totalRevenueGrowPercent}% from last month</span>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Subscribers Card */}
          <Card className="bg-background-surface border-white/10">
            <CardHeader className="pb-2">
              <CardTitle className="text-md font-medium flex items-center justify-between">
                <span>Subscribers</span>
                <Users className="h-4 w-4 text-primary" />
              </CardTitle>
              <CardDescription>Active subscriptions</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{statistics.totalActiveSubscriptionsCount}</div>
              {statistics.newThisWeekActiveSubscriptionsCount > 0 && (
                <div className="text-xs text-green-500 flex items-center mt-1">
                  <TrendingUp className="h-3 w-3 mr-1" />
                  <span>+{statistics.newThisWeekActiveSubscriptionsCount} new this week</span>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Content Card */}
          <Card className="bg-background-surface border-white/10">
            <CardHeader className="pb-2">
              <CardTitle className="text-md font-medium flex items-center justify-between">
                <span>Content</span>
                <Image className="h-4 w-4 text-primary" />
              </CardTitle>
              <CardDescription>Published items</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {statistics.publishedPremiumContentCount + statistics.publishedFreeContentCount}
              </div>
              <div className="text-xs text-text-secondary mt-1">
                <span>
                  {statistics.publishedPremiumContentCount} premium,{' '}
                  {statistics.publishedFreeContentCount} free
                </span>
              </div>
            </CardContent>
          </Card>

          {/* Engagement Card */}
          <Card className="bg-background-surface border-white/10">
            <CardHeader className="pb-2">
              <CardTitle className="text-md font-medium flex items-center justify-between">
                <span>Engagement</span>
                <Heart className="h-4 w-4 text-primary" />
              </CardTitle>
              <CardDescription>Likes & comments</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{statistics.engagement}</div>
              {statistics.engagementGrowthPercentage != 0 &&
                (statistics.engagementGrowthPercentage > 0 ? (
                  <div className="text-xs text-green-500 flex items-center mt-1">
                    <TrendingUp className="h-3 w-3 mr-1" />
                    <span>+{statistics.engagementGrowthPercentage}% from last month</span>
                  </div>
                ) : (
                  <div className="text-xs text-red-500 flex items-center mt-1">
                    <TrendingDown className="h-3 w-3 mr-1" />
                    <span>{statistics.engagementGrowthPercentage}% from last month</span>
                  </div>
                ))}
            </CardContent>
          </Card>
        </div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Revenue Chart */}
          <Card className="bg-background-surface border-white/10">
            <CardHeader className="pb-0">
              <CardTitle>Revenue Overview</CardTitle>
              <CardDescription>Monthly revenue trends</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={revenueData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
                    <XAxis dataKey="name" tick={{ fill: '#9CA3AF' }} />
                    <YAxis tick={{ fill: '#9CA3AF' }} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: '#1A1A1A',
                        border: '1px solid rgba(255,255,255,0.1)',
                        borderRadius: '0.5rem',
                        color: '#F5F5F5'
                      }}
                    />
                    <Line
                      type="monotone"
                      dataKey="value"
                      stroke="#6366F1"
                      strokeWidth={2}
                      dot={{ r: 4 }}
                      activeDot={{ r: 6 }}
                      name="Revenue ($)"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>

          {/* Subscribers Chart */}
          <Card className="bg-background-surface border-white/10">
            <CardHeader className="pb-0">
              <CardTitle>Subscriber Growth</CardTitle>
              <CardDescription>Monthly subscriber trends</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={subscribersData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
                    <XAxis dataKey="name" tick={{ fill: '#9CA3AF' }} />
                    <YAxis tick={{ fill: '#9CA3AF' }} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: '#1A1A1A',
                        border: '1px solid rgba(255,255,255,0.1)',
                        borderRadius: '0.5rem',
                        color: '#F5F5F5'
                      }}
                    />
                    <Bar dataKey="value" fill="#6366F1" radius={[4, 4, 0, 0]} name="Subscribers" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Bottom Row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Latest Posts */}
          <div className="lg:col-span-2">
            <Card className="bg-background-surface border-white/10 h-full">
              <CardHeader className="pb-2 flex flex-row items-center justify-between">
                <div>
                  <CardTitle>Latest Posts</CardTitle>
                  <CardDescription>Your recently published content</CardDescription>
                </div>
                <Button variant="ghost" size="sm" className="text-primary" asChild>
                  <Link href="/feed">
                    View All
                    <ChevronRight className="h-4 w-4 ml-1" />
                  </Link>
                </Button>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {latestPosts.map(post => (
                    <div
                      key={post.id}
                      className="flex items-center justify-between p-3 rounded-lg bg-background hover:bg-white/5 transition-colors"
                    >
                      <div className="flex-1">
                        <div className="flex items-center">
                          <h3 className="font-medium truncate">{post.description}</h3>
                          {post.type === 'PREMIUM' && (
                            <span className="ml-2 text-xs bg-premium-gradient text-white py-0.5 px-2 rounded-full">
                              Premium
                            </span>
                          )}
                        </div>
                        <div className="text-xs text-text-secondary mt-1">
                          {formatDate(new Date(post.createdAt))}
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="flex items-center text-sm">
                          <Heart className="h-4 w-4 mr-1 text-primary" />
                          <span>{post.likesCount}</span>
                        </div>
                        <div className="flex items-center text-sm">
                          <MessageCircle className="h-4 w-4 mr-1 text-primary" />
                          <span>{post.commentsCount}</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Upcoming Events */}
          <div>
            <Card className="bg-background-surface border-white/10 h-full">
              <CardHeader className="pb-2 flex flex-row items-center justify-between">
                <div>
                  <CardTitle>Upcoming Events</CardTitle>
                  <CardDescription>Scheduled activities</CardDescription>
                </div>
                <Button variant="ghost" size="sm" className="text-primary">
                  Add Event
                </Button>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {upcomingEvents.map(event => (
                    <div
                      key={event.id}
                      className="p-3 rounded-lg bg-background border border-white/10"
                    >
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="font-medium">{event.title}</h3>
                      </div>
                      <div className="flex items-center text-sm text-text-secondary">
                        <Calendar className="h-4 w-4 mr-2" />
                        <span>
                          {event.date} at {event.time}
                        </span>
                      </div>
                    </div>
                  ))}

                  {upcomingEvents.length === 0 && (
                    <div className="text-center py-4">
                      <Calendar className="h-8 w-8 mx-auto opacity-20 mb-2" />
                      <p className="text-text-secondary">No upcoming events</p>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </MainLayout>
  )
}

export default Dashboard
