export interface UserType {
  id: string
  username: string
  email: string
  givenName: string
  familyName: string
  bio: string | null
  avatarUrl: string | null
  socialMediaLinks: Partial<{
    INSTAGRAM: string
    TWITTER: string
    TIKTOK: string
    YOUTUBE: string
  }>
}

export interface UserProfile {
  id: string
  username: string
  email: string
  givenName: string
  familyName: string
  bio: string | null
  avatarUrl: string | null
  socialMediaLinks: Partial<{
    INSTAGRAM: string
    TWITTER: string
    TIKTOK: string
    YOUTUBE: string
  }>
  subscribedOn: Subscription
}

export interface SafeSaveMediaResponse {
  mediaUrl: string
  previewUrl: string
  safePreviewUrl: string
}

export interface MediaUrl {
  url: string
}

export const SubscriptionType = {
  FREE: 'FREE',
  PREMIUM: 'PREMIUM'
}

export type SubscriptionTypeType = (typeof SubscriptionType)[keyof typeof SubscriptionType]

export interface SubscriptionPlan {
  userId: string
  type: SubscriptionTypeType
  price: number
  createdAt: string
  updatedAt: string
}

export interface Subscription {
  subscriberId: string
  subscribedOnId: string
  subscriptionPlan: SubscriptionPlan
  cancelled: boolean
  createdAt: string
  expiredAt: string
}

export interface Content {
  id: string
  description: string
  mediaUrl: string
  previewUrl: string
  safePreviewUrl: string
  ownerId: string
  createdAt: string
}

export interface Media {
  url: string
  previewUrl: string
}

export interface NewPost {
  id: string
  description: string
  createdAt: string
  type: PostTypeType
  owner: UserType
  content: Content[]
}

export interface Post {
  id: string
  description: string
  createdAt: string
  type: PostTypeType
  likesCount: number
  commentsCount: number
  hasUserLike: boolean
  hasUserSubscription: boolean
  owner: UserType
  media: Media[]
  comments: Comment[]
}

export interface InvoiceUrl {
  invoiceUrl: string
}

export const PostType = {
  FREE: 'FREE',
  PREMIUM: 'PREMIUM'
}

export type PostTypeType = (typeof PostType)[keyof typeof PostType]

export interface Pageable<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
  }
  first: boolean
  last: boolean
  totalElements: number
  totalPages: number
}

export interface Comment {
  id: string
  content: string
  postId: string
  createdAt: string
  owner: UserType
}

export interface SubscribersInfo {
  totalSubscribers: number
}

export interface UserOverviewStatistics {
  totalRevenue: number
  totalRevenueGrowPercent: number
  totalRevenueByMonth: {
    key: string
    value: number
  }[]
  totalActiveSubscriptionsCount: number
  newThisWeekActiveSubscriptionsCount: number
  subscribersCountByMonth: {
    key: string
    value: number
  }[]
  publishedPremiumContentCount: number
  publishedFreeContentCount: number
  engagement: number
  engagementGrowthPercentage: number
}

export interface PostWithCounts {
  id: string
  description: string
  createdAt: string
  type: PostTypeType
  commentsCount: number
  likesCount: number
}

export interface UserAnalytics {
  revenueGrowth: {
    key: string
    value: number
  }[]
  subscriberGrowth: {
    key: string
    value: number
  }[]
  likesGrowth: {
    key: string
    value: number
  }[]
  commentsGrowth: {
    key: string
    value: number
  }[]
}
