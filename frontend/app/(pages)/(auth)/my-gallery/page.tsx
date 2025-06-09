'use client'

import { MoreVertical, Eye, Image, Search, User, PlusCircle, Loader2 } from 'lucide-react'
import { useRouter } from 'next/navigation'
import { signIn } from 'next-auth/react'
import React, { useCallback, useEffect, useState } from 'react'
import { toast } from 'sonner'
import MainLayout from '@/_components/layout/MainLayout'
import { contentApi } from '@/_shared/api/content'
import type { Content } from '@/_shared/api/types'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Button } from '@/_shared/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/_shared/ui/dialog'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '@/_shared/ui/dropdown-menu'

const Gallery = () => {
  const router = useRouter()

  const [content, setContent] = useState<Content[]>([])
  const [isLoading, setIsLoading] = useState(true)

  const user = useAuthStore(state => state.user)
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedFilter, setSelectedFilter] = useState<'all' | 'premium' | 'free'>('all')
  const [selectedContent, setSelectedContent] = useState<Content | null>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [isPricingDialogOpen, setIsPricingDialogOpen] = useState(false)
  const [isPreviewDialogOpen, setIsPreviewDialogOpen] = useState(false)
  const [price, setPrice] = useState('0')

  const fetchContent = useCallback(async () => {
    try {
      setIsLoading(true)

      const content = await contentApi.geyMyGallery()

      setContent(content)
    } catch {
      toast.error('Error fetching content')
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchContent()
  }, [fetchContent])

  const filteredContent = content.filter(contentItem => {
    return contentItem.description.toLowerCase().includes(searchQuery.toLowerCase())
  })

  const handlePreview = (contentItem: Content) => {
    setSelectedContent(contentItem)
    setIsPreviewDialogOpen(true)
  }

  if (!user) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <div className="text-center">
            <User className="h-16 w-16 mx-auto opacity-20 mb-4" />
            <h2 className="text-xl font-semibold">Not logged in</h2>
            <p className="text-text-secondary mt-2 mb-4">
              You need to sign in to view your gallery
            </p>
            <Button onClick={() => signIn('keycloak')}>Sign In</Button>
          </div>
        </div>
      </MainLayout>
    )
  }

  if (isLoading) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center h-[60vh]">
          <Loader2 className="h-10 w-10 animate-spin" />
        </div>
      </MainLayout>
    )
  }

  return (
    <MainLayout>
      <div className="container mx-auto max-w-6xl">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
          <div>
            <h1 className="text-2xl font-bold">My Gallery</h1>
            <p className="text-text-secondary">Manage your uploaded media</p>
          </div>

          <Button className="bg-premium-gradient" onClick={() => router.push('/new-content')}>
            <PlusCircle className="h-4 w-4 mr-2" />
            Create Content
          </Button>
        </div>

        <div className="mb-6 flex flex-col md:flex-row gap-4">
          <div className="relative flex-grow">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-text-secondary" />
            <input
              type="text"
              placeholder="Search by title..."
              className="pl-10 h-10 w-full rounded-lg border border-white/10 bg-background-surface focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        {filteredContent.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
            {filteredContent.map(contentItem => (
              <div
                key={contentItem.id}
                className="group relative overflow-hidden rounded-lg bg-background-surface border border-white/10"
              >
                <div
                  className="aspect-square overflow-hidden bg-background"
                  onClick={() => handlePreview(contentItem)}
                >
                  <img
                    src={contentItem.previewUrl}
                    alt={contentItem.description}
                    className="w-full h-full object-cover transition-transform duration-200 group-hover:scale-105"
                  />
                </div>

                <div className="p-4">
                  <div className="flex items-start justify-between">
                    <h3 className="font-medium text-text-primary truncate">
                      {contentItem.description}
                    </h3>

                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm" className="h-8 w-8 p-0 rounded-full">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="w-48">
                        <DropdownMenuItem onClick={() => handlePreview(contentItem)}>
                          <Eye className="h-4 w-4 mr-2" />
                          Preview
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-64 border border-white/5 rounded-xl bg-background-surface/50">
            <Image className="h-10 w-10 opacity-10 mb-2" />
            <p className="text-text-secondary mb-1">No images found</p>
            <p className="text-sm text-text-secondary mb-4">
              {searchQuery || selectedFilter !== 'all'
                ? 'Try adjusting your search or filters'
                : 'Upload some images to get started'}
            </p>
            {searchQuery || selectedFilter !== 'all' ? (
              <Button
                variant="outline"
                onClick={() => {
                  setSearchQuery('')
                  setSelectedFilter('all')
                }}
              >
                Clear Filters
              </Button>
            ) : (
              <Button className="bg-premium-gradient" onClick={() => router.push('/new-content')}>
                <PlusCircle className="h-4 w-4 mr-2" />
                Create Content
              </Button>
            )}
          </div>
        )}
      </div>

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent className="bg-background-surface border-white/10">
          <DialogHeader>
            <DialogTitle>Delete Image</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this image? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <div className="mt-2">
            <div className="h-40 w-40 mx-auto overflow-hidden rounded-md">
              {selectedContent && (
                <img
                  src={selectedContent.previewUrl}
                  alt={selectedContent.description}
                  className="object-cover w-full h-full"
                />
              )}
            </div>
          </div>
          <DialogFooter className="mt-4">
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)}>
              Cancel
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isPricingDialogOpen} onOpenChange={setIsPricingDialogOpen}>
        <DialogContent className="bg-background-surface border-white/10">
          <DialogHeader>
            <DialogTitle>Set Premium Content Price</DialogTitle>
            <DialogDescription>
              Set a price for this content. Users will have to pay to access it.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <label className="text-sm font-medium">Price (USD)</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 transform -translate-y-1/2">$</span>
                <input
                  type="number"
                  step="0.01"
                  min="0.99"
                  value={price}
                  onChange={e => setPrice(e.target.value)}
                  className="pl-8 h-10 w-full rounded-lg border border-white/10 bg-background focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </div>
              <p className="text-xs text-text-secondary mt-1">Minimum price is $0.99</p>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsPricingDialogOpen(false)}>
              Cancel
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isPreviewDialogOpen} onOpenChange={setIsPreviewDialogOpen}>
        <DialogContent className="bg-background-surface border-white/10 max-w-3xl">
          <DialogHeader>
            <DialogTitle>{selectedContent?.description}</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <div className="overflow-hidden rounded-md">
              {selectedContent && (
                <img
                  src={selectedContent.previewUrl}
                  alt={selectedContent.description}
                  className="object-cover w-full max-h-[60vh]"
                />
              )}
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </MainLayout>
  )
}

export default Gallery
