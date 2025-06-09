'use client'

import { Loader2, Search as SearchIcon } from 'lucide-react'
import { useRouter } from 'next/navigation'
import React, { useCallback, useEffect, useState } from 'react'
import { toast } from 'sonner'
import { useDebounce } from 'use-debounce'
import MainLayout from '@/_components/layout/MainLayout'
import type { UserType } from '@/_shared/api/types'
import { usersApi } from '@/_shared/api/users'
import { Avatar, AvatarFallback, AvatarImage } from '@/_shared/ui/avatar'
import { Card } from '@/_shared/ui/card'
import { Input } from '@/_shared/ui/input'
const Search = () => {
  const router = useRouter()

  const [isLoading, setIsLoading] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<UserType[]>([])

  const [debouncedSearch] = useDebounce(searchQuery, 500)

  const handleSearch = useCallback(async () => {
    setIsLoading(true)
    try {
      const results = await usersApi.searchUsers(debouncedSearch)

      setSearchResults(results)
    } catch {
      toast.error('Failed to search users')
    } finally {
      setIsLoading(false)
    }
  }, [debouncedSearch])

  const handleCardClick = useCallback((result: UserType) => {
    router.push(`/profile/${result.id}`)
  }, [router])

  useEffect(() => {
    if (debouncedSearch && debouncedSearch.length > 2) {
      handleSearch()
    }
  }, [debouncedSearch, handleSearch])

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <div className="relative mb-8">
          <SearchIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
          <Input
            className="pl-10"
            placeholder="Search creators, content..."
            autoFocus
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
          />
        </div>

        {isLoading ? (
          <div className="flex justify-center items-center h-40">
            <Loader2 className="w-6 h-6 animate-spin" />
          </div>
        ) : (
          <div className="space-y-4">
            {searchResults.map(result => (
              <Card
                key={result.id}
                className="p-4 bg-background-surface border-white/10 cursor-pointer hover:bg-white/5 transition-colors"
                onClick={() => handleCardClick(result)}
              >
                <div className="flex justify-between items-center">
                  <div className="flex items-center gap-3">
                    <Avatar>
                      <AvatarImage src={result.avatarUrl || undefined} />
                      <AvatarFallback>{result.givenName.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <div>
                      <h3 className="font-medium">
                        {result.givenName} {result.familyName}
                      </h3>
                      <p className="text-sm text-muted-foreground">{result.username}</p>
                    </div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </MainLayout>
  )
}

export default Search
