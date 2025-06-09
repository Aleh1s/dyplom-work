import { useCallback, useEffect } from 'react'
import useFeedStore from '../../_state/useFeedStore'
import type { PostTypeType } from '@/_shared/api/types'

export const useInfiniteFeed = () => {
  const { posts, hasMore, isLoading, error, fetchPosts, setFilter, resetFeed, selectedFilter } =
    useFeedStore()

  useEffect(() => {
    if (posts.length === 0) {
      fetchPosts()
    }
  }, [posts.length, fetchPosts])

  const handleLoadMore = useCallback(() => {
    if (!isLoading && hasMore) {
      fetchPosts()
    }
  }, [isLoading, hasMore, fetchPosts])

  const handleFilterChange = useCallback(
    async (filter: PostTypeType | 'ALL') => {
      await setFilter(filter)
    },
    [setFilter]
  )

  return {
    posts,
    hasMore,
    isLoading,
    error,
    handleLoadMore,
    handleFilterChange,
    selectedFilter,
    resetFeed
  }
}
