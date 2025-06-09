'use client'

import { Loader2, User, Filter, Image as ImageIcon } from 'lucide-react'
import { signIn } from 'next-auth/react'
import React, { useState, useEffect, useCallback } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { useInView } from 'react-intersection-observer'
import { GalleryDialog } from './_components/GalleryDialog'
import { useInfiniteFeed } from './_hooks/useInfiniteFeed'
import useFeedStore from './_state/useFeedStore'
import MainLayout from '@/_components/layout/MainLayout'
import { toast } from '@/_hooks/use-toast'
import type { Content } from '@/_shared/api/types'
import { PostType } from '@/_shared/api/types'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Avatar, AvatarImage, AvatarFallback } from '@/_shared/ui/avatar'
import { Button } from '@/_shared/ui/button'
import { Label } from '@/_shared/ui/label'
import PostCard from '@/_shared/ui/post-card'
import { Switch } from '@/_shared/ui/switch'
import { Textarea } from '@/_shared/ui/textarea'

interface CreatePostFormData {
  description: string
  content: Content[]
  isPremium: boolean
}

const ContentFeed = () => {
  const user = useAuthStore(state => state.user)

  const { posts, handleLoadMore, handleFilterChange, selectedFilter, resetFeed } = useInfiniteFeed()

  const {
    control,
    setValue,
    watch,
    handleSubmit,
    reset: resetForm,
    formState: { isValid, isSubmitting }
  } = useForm<CreatePostFormData>({
    defaultValues: {
      description: '',
      content: [],
      isPremium: false
    }
  })

  const content = watch('content')

  const likePost = useFeedStore(state => state.likePost)
  const unlikePost = useFeedStore(state => state.unlikePost)
  const commentPost = useFeedStore(state => state.commentPost)
  const createPost = useFeedStore(state => state.createPost)

  const [showScrollToTop, setShowScrollToTop] = useState(false)
  const [isGalleryOpen, setIsGalleryOpen] = useState(false)

  const { ref: loadMoreRef } = useInView({
    onChange: inView => {
      if (inView) {
        handleLoadMore()
      }
    }
  })

  const removeContent = (index: number) => {
    setValue(
      'content',
      content.filter((_, i) => i !== index)
    )
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const handleGallerySelect = useCallback((selectedContent: Content[]) => {
    setValue('content', [
      ...content,
      ...selectedContent.filter(selected => !content.some(content => content.id === selected.id))
    ])
    setIsGalleryOpen(false)
  }, [])

  const onCreatePost = handleSubmit(async data => {
    createPost({
      description: data.description,
      contentIds: data.content.map(content => content.id),
      type: data.isPremium ? PostType.PREMIUM : PostType.FREE
    })
      .then(() => {
        resetForm()
        resetFeed()

        toast({
          title: 'Post created',
          description: 'Your post has been published successfully.'
        })
      })
      .catch(() => {
        toast({
          title: 'Post failed',
          description: 'Failed to publish your post. Please try again.',
          variant: 'destructive'
        })

        toast({
          title: 'Post failed',
          description: 'Failed to publish your post. Please try again.',
          variant: 'destructive'
        })
      })
  })

  useEffect(() => {
    const handleScroll = () => {
      setShowScrollToTop(window.scrollY > 500)
    }

    window.addEventListener('scroll', handleScroll)

    return () => {
      window.removeEventListener('scroll', handleScroll)
    }
  }, [])

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  if (!user) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <div className="text-center">
            <User className="h-16 w-16 mx-auto opacity-20 mb-4" />
            <h2 className="text-xl font-semibold">Not logged in</h2>
            <p className="text-text-secondary mt-2 mb-4">
              You need to sign in to view the content feed
            </p>
            <Button onClick={() => signIn('keycloak')}>Sign In</Button>
          </div>
        </div>
      </MainLayout>
    )
  }

  return (
    <MainLayout>
      <div className="max-w-3xl mx-auto">
        <div className="bg-background-surface rounded-xl border border-white/10 p-4 mb-6">
          <div className="flex items-start gap-3">
            <Avatar className="h-10 w-10">
              <AvatarImage src={user.avatarUrl || undefined} alt={user.username} />
              <AvatarFallback>{user.username?.[0]?.toUpperCase()}</AvatarFallback>
            </Avatar>

            <div className="flex-grow">
              <Controller
                control={control}
                name="description"
                rules={{
                  required: true,
                  minLength: {
                    value: 3,
                    message: 'Description must be at least 3 characters long'
                  },
                  maxLength: {
                    value: 3000,
                    message: 'Description must be less than 3000 characters long'
                  }
                }}
                render={({ field, fieldState: { error } }) => (
                  <>
                    <Textarea
                      placeholder="Share something with your audience..."
                      className={`resize-none bg-background border-white/10 mb-3 ${error ? 'border-red-500' : ''}`}
                      {...field}
                    />
                    {error && <p className="text-red-500 text-sm">{error.message}</p>}
                  </>
                )}
              />

              <div className="flex flex-wrap gap-2 mb-3">
                {content.map((content, index) => (
                  <div
                    key={index}
                    className="relative w-[150px] rounded-xl border border-white/10 overflow-hidden"
                  >
                    <div className="aspect-square">
                      <img
                        src={content.previewUrl}
                        alt="Preview"
                        className="w-full h-full object-cover"
                      />
                    </div>
                    <button
                      onClick={() => removeContent(index)}
                      className="absolute top-2 right-2 bg-background/80 hover:bg-background/90 rounded-full p-1.5 transition-colors"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M6 18L18 6M6 6l12 12"
                        />
                      </svg>
                    </button>
                  </div>
                ))}
              </div>

              <div className="flex flex-wrap items-center gap-3 justify-between">
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex items-center gap-2"
                    onClick={() => setIsGalleryOpen(true)}
                  >
                    <ImageIcon className="h-4 w-4" />
                    <span>From Gallery</span>
                  </Button>
                  <div className="flex items-center gap-2">
                    <Controller
                      control={control}
                      name="isPremium"
                      rules={{
                        required: false
                      }}
                      render={({ field }) => (
                        <Switch
                          id="premium-toggle"
                          checked={field.value}
                          onCheckedChange={field.onChange}
                        />
                      )}
                    />
                    <Label htmlFor="premium-toggle" className="text-sm">
                      Premium
                    </Label>
                  </div>
                </div>

                <Button
                  onClick={onCreatePost}
                  disabled={isSubmitting || !isValid || content.length === 0}
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                      <span>Posting...</span>
                    </>
                  ) : (
                    'Post'
                  )}
                </Button>
              </div>
            </div>
          </div>
        </div>

        <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
          <Button
            variant={selectedFilter === 'ALL' ? 'default' : 'outline'}
            size="sm"
            onClick={() => handleFilterChange('ALL')}
          >
            All Posts
          </Button>
          <Button
            variant={selectedFilter === 'PREMIUM' ? 'default' : 'outline'}
            size="sm"
            onClick={() => handleFilterChange('PREMIUM')}
          >
            Premium Only
          </Button>
          <Button
            variant={selectedFilter === 'FREE' ? 'default' : 'outline'}
            size="sm"
            onClick={() => handleFilterChange('FREE')}
          >
            Free Content
          </Button>
        </div>

        <div className="space-y-6">
          {posts.length > 0 ? (
            posts.map(post => (
              <PostCard
                key={post.id}
                post={post}
                likePost={likePost}
                unlikePost={unlikePost}
                commentPost={commentPost}
              />
            ))
          ) : (
            <div className="flex flex-col items-center justify-center p-8 bg-background-surface border border-white/10 rounded-xl text-center">
              <Filter className="h-10 w-10 opacity-10 mb-2" />
              <h3 className="text-lg font-medium mb-1">No posts found</h3>
              <p className="text-text-secondary">
                {selectedFilter !== 'ALL'
                  ? 'Try changing your filter selection'
                  : 'Be the first to create a post'}
              </p>
              {selectedFilter !== 'ALL' && (
                <Button
                  variant="outline"
                  className="mt-4"
                  onClick={() => handleFilterChange('ALL')}
                >
                  Show all posts
                </Button>
              )}
            </div>
          )}

          <div ref={loadMoreRef}></div>
        </div>

        {showScrollToTop && (
          <button
            className="fixed bottom-8 right-8 bg-primary text-white p-3 rounded-full shadow-lg hover:bg-primary/90 transition-all"
            onClick={scrollToTop}
            aria-label="Scroll to top"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 15l7-7 7 7"
              />
            </svg>
          </button>
        )}

        <GalleryDialog
          isOpen={isGalleryOpen}
          onClose={() => setIsGalleryOpen(false)}
          onSelect={handleGallerySelect}
        />
      </div>
    </MainLayout>
  )
}

export default ContentFeed
