import { Home, Image, Settings, ChevronLeft, ChevronRight, LineChart, Heart } from 'lucide-react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import React from 'react'
import { cn } from '@/_lib/utils'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Button } from '@/_shared/ui/button'

interface SidebarProps {
  isOpen: boolean
  toggleSidebar: () => void
}

interface NavItem {
  path: string
  label: string
  icon: React.ReactNode
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen, toggleSidebar }) => {
  const user = useAuthStore(state => state.user)
  const pathname = usePathname()

  const navItems: NavItem[] = [
    { path: '/', label: 'Home', icon: <Home className="w-5 h-5" /> },
    { path: '/feed', label: 'Content Feed', icon: <Heart className="w-5 h-5" /> },
    { path: '/my-gallery', label: 'My Gallery', icon: <Image className="w-5 h-5" /> },
    { path: '/analytics', label: 'Analytics', icon: <LineChart className="w-5 h-5" /> },
    { path: '/settings', label: 'Settings', icon: <Settings className="w-5 h-5" /> }
  ]

  const isActive = (path: string) => {
    if (path === '/' && pathname === '/') return true
    if (path !== '/' && pathname?.startsWith(path)) return true

    return false
  }

  return (
    <>
      {/* Mobile sidebar backdrop */}
      {isOpen && (
        <div className="md:hidden fixed inset-0 bg-black/50 z-40" onClick={toggleSidebar} />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          'fixed md:sticky top-0 left-0 z-50 md:z-0 h-screen bg-background-surface border-r border-white/10',
          'transition-all duration-300 flex flex-col',
          isOpen ? 'w-64 translate-x-0' : 'w-0 md:w-16 -translate-x-full md:translate-x-0'
        )}
      >
        {/* Sidebar Header */}
        <div className="h-16 px-4 flex items-center justify-between border-b border-white/10">
          {isOpen && (
            <Link href="/" className="text-lg font-bold text-gradient">
              ReShare
            </Link>
          )}
          <Button
            variant="ghost"
            size="icon"
            className={cn('rounded-full', !isOpen && 'md:mx-auto')}
            onClick={toggleSidebar}
          >
            {isOpen ? <ChevronLeft className="h-5 w-5" /> : <ChevronRight className="h-5 w-5" />}
          </Button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 py-4 overflow-y-auto">
          <ul className="space-y-1 px-2">
            {navItems.map(item => (
              <li key={item.path}>
                <Link
                  href={user ? item.path : '/login'}
                  className={cn(
                    'flex items-center gap-3 px-3 py-2 rounded-lg transition-all',
                    isActive(item.path)
                      ? 'bg-primary text-white'
                      : 'text-text-secondary hover:bg-white/5',
                    !isOpen && 'md:justify-center md:px-0'
                  )}
                >
                  {item.icon}
                  {isOpen && <span>{item.label}</span>}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      </aside>
    </>
  )
}

export default Sidebar
