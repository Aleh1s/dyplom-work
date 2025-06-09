import { Search, Image } from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { toast } from 'sonner'
import { contentApi } from '@/_shared/api/content'
import type { Content } from '@/_shared/api/types'
import { Button } from '@/_shared/ui/button'
import { Checkbox } from '@/_shared/ui/checkbox'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/_shared/ui/dialog'

interface GalleryDialogProps {
  isOpen: boolean
  onClose: () => void
  onSelect: (content: Content[]) => void
}

export const GalleryDialog = ({ isOpen, onClose, onSelect }: GalleryDialogProps) => {
  const [content, setContent] = useState<Content[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedContent, setSelectedContent] = useState<Set<string>>(new Set())

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
    if (isOpen) {
      fetchContent()
      setSelectedContent(new Set()) // Reset selection when dialog opens
    }
  }, [isOpen, fetchContent])

  const filteredContent = content.filter(contentItem => {
    return contentItem.description.toLowerCase().includes(searchQuery.toLowerCase())
  })

  const toggleContent = (contentId: string) => {
    const newSelected = new Set(selectedContent)

    if (newSelected.has(contentId)) {
      newSelected.delete(contentId)
    } else {
      newSelected.add(contentId)
    }
    setSelectedContent(newSelected)
  }

  const handleConfirm = () => {
    const selectedItems = content.filter(item => selectedContent.has(item.id))

    onSelect(selectedItems)
    onClose()
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="bg-background-surface border-white/10 max-w-4xl">
        <DialogHeader>
          <DialogTitle>Select from Gallery</DialogTitle>
        </DialogHeader>

        <div className="relative mb-4">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-text-secondary" />
          <input
            type="text"
            placeholder="Search by description..."
            className="pl-10 h-10 w-full rounded-lg border border-white/10 bg-background focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
          />
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : filteredContent.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 max-h-[60vh] overflow-y-auto p-1">
            {filteredContent.map(contentItem => (
              <div
                key={contentItem.id}
                className="group relative overflow-hidden rounded-lg bg-background border border-white/10 hover:border-primary transition-colors"
              >
                <div className="absolute top-2 left-2 z-10" onClick={e => e.stopPropagation()}>
                  <Checkbox
                    checked={selectedContent.has(contentItem.id)}
                    onCheckedChange={() => toggleContent(contentItem.id)}
                  />
                </div>
                {selectedContent.has(contentItem.id) && (
                  <div className="absolute inset-0 bg-primary/20 z-[1]" />
                )}
                <div
                  className="aspect-square overflow-hidden bg-background cursor-pointer"
                  onClick={() => toggleContent(contentItem.id)}
                >
                  <img
                    src={contentItem.previewUrl}
                    alt={contentItem.description}
                    className="w-full h-full object-cover transition-transform duration-200 group-hover:scale-105"
                  />
                </div>
                <div className="p-2">
                  <p className="text-sm truncate">{contentItem.description}</p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-64 border border-white/5 rounded-xl bg-background/50">
            <Image className="h-10 w-10 opacity-10 mb-2" />
            <p className="text-text-secondary mb-1">No images found</p>
            <p className="text-sm text-text-secondary mb-4">
              {searchQuery ? 'Try adjusting your search' : 'Upload some images to get started'}
            </p>
            {searchQuery && (
              <Button variant="outline" onClick={() => setSearchQuery('')}>
                Clear Search
              </Button>
            )}
          </div>
        )}

        <DialogFooter>
          <div className="flex justify-between items-center w-full">
            <p className="text-sm text-text-secondary">
              {selectedContent.size} item{selectedContent.size !== 1 ? 's' : ''} selected
            </p>
            <div className="flex gap-2">
              <Button variant="outline" onClick={onClose}>
                Cancel
              </Button>
              <Button onClick={handleConfirm} disabled={selectedContent.size === 0}>
                Add Selected
              </Button>
            </div>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
