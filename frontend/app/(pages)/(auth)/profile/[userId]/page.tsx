'use client'

import {
  Instagram,
  Twitter,
  Youtube,
  Link as LinkIcon,
  Pencil,
  Camera,
  User,
  Heart,
  Loader2
} from 'lucide-react'
import { signIn } from 'next-auth/react'
import type { FC } from 'react'
import React, { useEffect, useState } from 'react'
import { toast } from 'sonner'
import useProfileStore from './_state/useProfileStore'
import MainLayout from '@/_components/layout/MainLayout'
import { mediaApi } from '@/_shared/api/media'
import { SubscriptionType } from '@/_shared/api/types'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Avatar, AvatarFallback, AvatarImage } from '@/_shared/ui/avatar'
import { Button } from '@/_shared/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger
} from '@/_shared/ui/dialog'
import { Input } from '@/_shared/ui/input'
import PostCard from '@/_shared/ui/post-card'
import { Textarea } from '@/_shared/ui/textarea'

interface Props {
  params: Promise<{
    userId: string
  }>
}

const formatDate = (date: Date) => {
  return date.toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  })
}

const Profile: FC<Props> = ({ params }) => {
  const { userId } = React.use(params)

  const user = useAuthStore(state => state.user)
  const updateMe = useAuthStore(state => state.updateMe)

  const {
    userProfile,
    posts,
    subscriptionPlan,
    isUserProfileLoading,
    isPostsLoading,
    isSubscriptionPlanLoading,
    totalSubscribers,
    fetchSubscriptionPlan,
    fetchUserProfile,
    fetchPosts,
    likePost,
    unlikePost,
    commentPost,
    updateUserProfile,
    subscribe,
    unsubscribe,
    fetchTotalSubscribers,
    createSubscriptionsInvoice
  } = useProfileStore()

  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [formData, setFormData] = useState({
    givenName: user?.givenName || '',
    familyName: user?.familyName || '',
    bio: user?.bio || '',
    instagram: user?.socialMediaLinks?.INSTAGRAM || '',
    twitter: user?.socialMediaLinks?.TWITTER || '',
    youtube: user?.socialMediaLinks?.YOUTUBE || ''
  })

  const [isSubmitting, setIsSubmitting] = useState(false)

  useEffect(() => {
    if (!userId) return

    fetchUserProfile(userId)
    fetchPosts(userId)
    fetchSubscriptionPlan(userId)
    fetchTotalSubscribers(userId)
  }, [userId, fetchUserProfile, fetchPosts, fetchSubscriptionPlan, fetchTotalSubscribers])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target

    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleEditProfile = async () => {
    setIsSubmitting(true)
    try {
      const updatedUser = await updateMe({
        givenName: formData.givenName,
        familyName: formData.familyName,
        bio: formData.bio,
        socialMediaLinks: {
          INSTAGRAM: formData.instagram,
          TWITTER: formData.twitter,
          YOUTUBE: formData.youtube
        }
      })

      if (updatedUser) {
        updateUserProfile(updatedUser)
      }

      setIsEditDialogOpen(false)
      toast.success('Profile updated', {
        description: 'Your profile has been updated successfully.'
      })
    } catch {
      toast.error('Update failed', {
        description: 'Failed to update profile. Please try again.'
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleProfilePictureChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]

    if (!file) return

    const { url } = await mediaApi.upload(file)

    try {
      const updatedUser = await updateMe({
        avatarUrl: url
      })

      if (updatedUser) {
        updateUserProfile(updatedUser)
      }

      toast.success('Profile picture updated', {
        description: 'Your profile picture has been updated successfully.'
      })
    } catch {
      toast.error('Update failed', {
        description: 'Failed to update profile picture. Please try again.'
      })
    }
  }

  const handleSubscribe = async () => {
    if (!userProfile) return

    if (subscriptionPlan?.type === SubscriptionType.PREMIUM) {
      await createSubscriptionsInvoice(userProfile.id)
    } else if (subscriptionPlan?.type === SubscriptionType.FREE) {
      await subscribe(userProfile.id)
    }
  }

  if (isUserProfileLoading || isSubscriptionPlanLoading) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <Loader2 className="h-16 w-16 animate-spin" />
        </div>
      </MainLayout>
    )
  }

  if (!userProfile) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <div className="text-center">
            <User className="h-16 w-16 mx-auto opacity-20 mb-4" />
            <h2 className="text-xl font-semibold">Not logged in</h2>
            <p className="text-text-secondary mt-2 mb-4">You need to sign in to view this page</p>
            <Button onClick={() => signIn('keycloak')}>Sign In</Button>
          </div>
        </div>
      </MainLayout>
    )
  }

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <div className="h-48 md:h-64 bg-gradient-to-r from-purple-900/30 to-blue-900/30 rounded-2xl mb-16 relative overflow-hidden">
          <div className="absolute inset-0 bg-premium-gradient opacity-10"></div>
        </div>

        <div className="relative px-6 -mt-24 pb-8">
          <div className="relative inline-block">
            <Avatar className="h-32 w-32 border-4 border-background">
              <AvatarImage
                src={userProfile.avatarUrl || undefined}
                alt={userProfile.username}
                className="object-cover"
              />
              <AvatarFallback className="text-4xl">
                {userProfile.username?.[0].toUpperCase()}
              </AvatarFallback>
            </Avatar>

            {userProfile.id === user?.id && (
              <label
                className="absolute bottom-0 right-0 p-1.5 rounded-full bg-primary text-white cursor-pointer"
                title="Change profile picture"
              >
                <Camera className="h-4 w-4" />
                <input
                  type="file"
                  className="hidden"
                  accept="image/*"
                  onChange={handleProfilePictureChange}
                />
              </label>
            )}
          </div>

          <div className="mt-4 flex flex-wrap items-start justify-between gap-4">
            <div>
              <h1 className="text-2xl font-bold">{userProfile.username}</h1>
              <p className="text-text-secondary">{userProfile.email}</p>

              <div className="flex items-center gap-4 mt-4">
                <div className="flex items-center gap-2">
                  <Heart className="h-4 w-4 text-text-secondary" />
                  <span className="text-sm text-text-secondary">
                    {totalSubscribers} subscribers
                  </span>
                </div>

                {userProfile.bio && <p className="text-text-primary">{userProfile.bio}</p>}
              </div>

              {(userProfile.socialMediaLinks?.INSTAGRAM ||
                userProfile.socialMediaLinks?.TWITTER ||
                userProfile.socialMediaLinks?.YOUTUBE) && (
                  <div className="mt-4 flex gap-4">
                    {userProfile.socialMediaLinks?.INSTAGRAM && (
                      <a
                        href={`https://instagram.com/${userProfile.socialMediaLinks.INSTAGRAM}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-text-secondary hover:text-primary"
                        title="Instagram"
                      >
                        <Instagram className="h-5 w-5" />
                      </a>
                    )}

                    {userProfile.socialMediaLinks?.TWITTER && (
                      <a
                        href={`https://twitter.com/${userProfile.socialMediaLinks.TWITTER}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-text-secondary hover:text-primary"
                        title="Twitter"
                      >
                        <Twitter className="h-5 w-5" />
                      </a>
                    )}

                    {userProfile.socialMediaLinks?.YOUTUBE && (
                      <a
                        href={`https://youtube.com/@${userProfile.socialMediaLinks.YOUTUBE}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-text-secondary hover:text-primary"
                        title="YouTube"
                      >
                        <Youtube className="h-5 w-5" />
                      </a>
                    )}
                  </div>
                )}
            </div>

            <div className="flex gap-3">
              {userProfile.id !== user?.id &&
                (userProfile.subscribedOn ? (
                  <Button
                    variant="outline"
                    className="flex gap-2"
                    onClick={() => {
                      if (!userProfile.subscribedOn.cancelled) {
                        unsubscribe(userProfile.id)
                      }
                    }}
                  >
                    <Heart className="h-4 w-4" />
                    {!userProfile.subscribedOn.cancelled ? (
                      <span>Unsubscribe</span>
                    ) : (
                      <span>
                        Subscription ends {formatDate(new Date(userProfile.subscribedOn.expiredAt))}
                      </span>
                    )}
                  </Button>
                ) : (
                  <Button variant="default" className="flex gap-2" onClick={handleSubscribe}>
                    <Heart className="h-4 w-4" />
                    <span>
                      Subscribe{' '}
                      {subscriptionPlan?.type === SubscriptionType.PREMIUM
                        ? `for ${subscriptionPlan.price}$`
                        : 'for free'}
                    </span>
                  </Button>
                ))}

              <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
                {userProfile.id === user?.id && (
                  <DialogTrigger asChild>
                    <Button variant="default" className="flex gap-2">
                      <Pencil className="h-4 w-4" />
                      <span>Edit Profile</span>
                    </Button>
                  </DialogTrigger>
                )}
                <DialogContent className="sm:max-w-[425px] bg-background-surface border-white/10">
                  <DialogHeader>
                    <DialogTitle>Edit Profile</DialogTitle>
                    <DialogDescription>
                      Update your profile information and social links
                    </DialogDescription>
                  </DialogHeader>

                  <div className="space-y-4 py-4">
                    <div className="space-y-2">
                      <label className="text-sm font-medium">First Name</label>
                      <Input
                        name="givenName"
                        value={formData.givenName}
                        onChange={handleInputChange}
                        className="bg-background border-white/10"
                      />
                    </div>

                    <div className="space-y-2">
                      <label className="text-sm font-medium">Last Name</label>
                      <Input
                        name="familyName"
                        value={formData.familyName}
                        onChange={handleInputChange}
                        className="bg-background border-white/10"
                      />
                    </div>

                    <div className="space-y-2">
                      <label className="text-sm font-medium">Bio</label>
                      <Textarea
                        name="bio"
                        value={formData.bio}
                        onChange={handleInputChange}
                        className="bg-background border-white/10 min-h-[100px]"
                        placeholder="Tell others about yourself..."
                      />
                    </div>

                    <div className="space-y-4">
                      <h4 className="text-sm font-medium">Social Links</h4>

                      <div className="space-y-2">
                        <div className="flex items-center gap-2">
                          <Instagram className="h-4 w-4 text-pink-500" />
                          <label className="text-sm text-text-secondary">Instagram Username</label>
                        </div>
                        <Input
                          name="instagram"
                          value={formData.instagram}
                          onChange={handleInputChange}
                          className="bg-background border-white/10"
                          placeholder="username (without @)"
                        />
                      </div>

                      <div className="space-y-2">
                        <div className="flex items-center gap-2">
                          <Twitter className="h-4 w-4 text-blue-400" />
                          <label className="text-sm text-text-secondary">Twitter Username</label>
                        </div>
                        <Input
                          name="twitter"
                          value={formData.twitter}
                          onChange={handleInputChange}
                          className="bg-background border-white/10"
                          placeholder="username (without @)"
                        />
                      </div>

                      <div className="space-y-2">
                        <div className="flex items-center gap-2">
                          <Youtube className="h-4 w-4 text-red-500" />
                          <label className="text-sm text-text-secondary">YouTube Username</label>
                        </div>
                        <Input
                          name="youtube"
                          value={formData.youtube}
                          onChange={handleInputChange}
                          className="bg-background border-white/10"
                          placeholder="username (without @)"
                        />
                      </div>
                    </div>
                  </div>

                  <DialogFooter>
                    <Button variant="outline" onClick={() => setIsEditDialogOpen(false)}>
                      Cancel
                    </Button>
                    <Button onClick={handleEditProfile} disabled={isSubmitting}>
                      {isSubmitting ? 'Saving...' : 'Save changes'}
                    </Button>
                  </DialogFooter>
                </DialogContent>
              </Dialog>
            </div>
          </div>
        </div>

        {isPostsLoading ? (
          <div className="flex items-center justify-center h-48">
            <Loader2 className="h-16 w-16 animate-spin" />
          </div>
        ) : (
          <div className="px-6 mt-4 border-t border-white/10 pt-8">
            <h2 className="text-xl font-bold mb-6">Latest Content</h2>
            {posts.length > 0 ? (
              <div className="space-y-6">
                {posts.map(post => (
                  <PostCard
                    key={post.id}
                    post={post}
                    likePost={likePost}
                    unlikePost={unlikePost}
                    commentPost={commentPost}
                  />
                ))}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center h-48 border border-white/5 rounded-xl bg-background-surface/50">
                <LinkIcon className="h-10 w-10 opacity-10 mb-2" />
                <p className="text-text-secondary">No content published yet</p>
                {userProfile.id === user?.id && (
                  <Button variant="link" className="mt-2" asChild>
                    <a href="/feed">Create your first content</a>
                  </Button>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </MainLayout>
  )
}

export default Profile
