'use client'

import { Lock, DollarSign, Heart, MessageCircle } from 'lucide-react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import type { FC } from 'react'
import { useState } from 'react'
import type { Post } from '../api/types'
import { formatDate } from '../lib/formatDate'
import useAuthStore from '../state/useAuthStore'
import { Avatar, AvatarFallback, AvatarImage } from '@/_shared/ui/avatar'
import { Button } from '@/_shared/ui/button'
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious
} from '@/_shared/ui/carousel'
import { Input } from '@/_shared/ui/input'

interface Props {
  post: Post
  likePost: (postId: string) => void
  unlikePost: (postId: string) => void
  commentPost: (postId: string, comment: string) => void
}

const PostCard: FC<Props> = ({ post, likePost, unlikePost, commentPost }) => {
  const router = useRouter()

  const user = useAuthStore(state => state.user)

  const [newComment, setNewComment] = useState('')

  const handleComment = (postId: string) => {
    commentPost(postId, newComment)
    setNewComment('')
  }

  const goToUserProfile = () => {
    router.push(`/profile/${post.owner.id}`)
  }

  return (
    <div
      key={post.id}
      className="bg-background-surface rounded-xl border border-white/10 overflow-hidden"
    >
      <div className="p-4 flex items-center gap-3">
        <Avatar className="h-10 w-10 rounded-full cursor-pointer" onClick={goToUserProfile}>
          <AvatarImage src={post.owner.avatarUrl || undefined} alt={post.owner.username} />
          <AvatarFallback>{post.owner.username?.[0]?.toUpperCase()}</AvatarFallback>
        </Avatar>
        <div>
          <Link href={`/profile/${post.owner.id}`}>
            <h3 className="font-medium">{post.owner.username}</h3>
          </Link>
          <p className="text-xs text-text-secondary">{formatDate(new Date(post.createdAt))}</p>
        </div>
        {post.type === 'PREMIUM' && (
          <div className="ml-auto">
            <span className="bg-premium-gradient text-white text-xs py-1 px-2 rounded-full flex items-center gap-1">
              <DollarSign className="h-3 w-3" />
              <span>Premium</span>
            </span>
          </div>
        )}
      </div>

      <div className="px-4 pb-2">
        <p className="whitespace-pre-line mb-3">{post.description}</p>
      </div>

      {post.media.length > 0 && (
        <div className="relative">
          {post.type === 'PREMIUM' && !post.hasUserSubscription && (
            <div className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-70 z-10">
              <Lock className="h-8 w-8 mb-2 text-primary" />
              <p className="text-white text-center mb-2 font-medium">Premium Content</p>
              <p className="text-text-secondary text-center text-sm mb-4 max-w-xs">
                Subscribe to get full access
              </p>
              <Button className="bg-premium-gradient hover:opacity-90" onClick={goToUserProfile}>
                <DollarSign className="h-4 w-4 mr-1" />
                <span>Subscribe</span>
              </Button>
            </div>
          )}

          <div>
            {post.media.length === 1 ? (
              <div className="w-full">
                <img src={post.media[0].previewUrl} alt="Post content" className="w-full" />
              </div>
            ) : (
              <Carousel className="w-full">
                <CarouselContent>
                  {post.media.map((media, index: number) => (
                    <CarouselItem key={index}>
                      <div className="w-full">
                        <img
                          src={media.previewUrl}
                          alt={`Post content ${index + 1}`}
                          className="w-full"
                        />
                      </div>
                    </CarouselItem>
                  ))}
                </CarouselContent>
                {post.media.length > 1 && (
                  <>
                    <CarouselPrevious className="left-2" />
                    <CarouselNext className="right-2" />
                  </>
                )}
              </Carousel>
            )}
          </div>
        </div>
      )}

      <div className="p-4 border-t border-white/5 flex items-center gap-4">
        <button
          className={`flex items-center gap-1 text-sm ${post.hasUserLike ? 'text-primary' : 'text-text-secondary'}`}
          onClick={() => (post.hasUserLike ? unlikePost(post.id) : likePost(post.id))}
        >
          <Heart className={`h-5 w-5 ${post.hasUserLike ? 'fill-primary' : ''}`} />
          <span>{post.likesCount}</span>
        </button>

        <button className="flex items-center gap-1 text-sm text-text-secondary">
          <MessageCircle className="h-5 w-5" />
          <span>{post.commentsCount}</span>
        </button>
      </div>

      {post.comments.length > 0 && (
        <div className="px-4 pb-3 border-t border-white/5">
          <div className="pr-4 max-h-[300px] overflow-y-auto">
            {post.comments.map(comment => (
              <div key={comment.id} className="py-3 flex gap-3">
                <Avatar className="h-8 w-8 rounded-full">
                  <AvatarImage
                    src={comment.owner.avatarUrl || undefined}
                    alt={comment.owner.username}
                  />
                  <AvatarFallback>{comment.owner.username?.[0]?.toUpperCase()}</AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <div className="bg-background rounded-lg py-2 px-3">
                    <div className="font-medium text-sm mb-1">{comment.owner.username}</div>
                    <p className="text-sm">{comment.content}</p>
                  </div>
                  <div className="text-xs text-text-secondary mt-1 ml-3">
                    {formatDate(new Date(comment.createdAt))}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="p-4 pt-2 border-t border-white/5 flex gap-3">
        <Avatar className="h-8 w-8 rounded-full">
          <AvatarImage src={user?.avatarUrl || undefined} alt={user?.username} />
          <AvatarFallback>{user?.username?.[0]?.toUpperCase()}</AvatarFallback>
        </Avatar>
        <div className="flex-1 flex items-center gap-2">
          <Input
            placeholder="Write a comment..."
            className="bg-background border-white/10"
            value={newComment}
            onChange={e => setNewComment(e.target.value)}
            onKeyDown={e => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault()
                handleComment(post.id)
              }
            }}
          />
          <Button size="sm" onClick={() => handleComment(post.id)} disabled={!newComment}>
            Post
          </Button>
        </div>
      </div>
    </div>
  )
}

export default PostCard
