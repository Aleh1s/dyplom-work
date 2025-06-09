import { Menu, Search } from 'lucide-react'
import Link from 'next/link'
import { signIn } from 'next-auth/react'
import React from 'react'
import logout from '@/_shared/lib/logout'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Avatar, AvatarFallback, AvatarImage } from '@/_shared/ui/avatar'
import { Button } from '@/_shared/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/_shared/ui/dropdown-menu'

interface NavbarProps {
  onMenuClick: () => void
}

const Navbar: React.FC<NavbarProps> = ({ onMenuClick }) => {
  const user = useAuthStore(state => state.user)

  return (
    <header className="sticky top-0 z-30 bg-background/80 backdrop-blur-md border-b border-white/10 py-3">
      <div className="container flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            onClick={onMenuClick}
            aria-label="Toggle menu"
          >
            <Menu className="h-5 w-5" />
          </Button>

          <Link href="/" className="text-xl font-bold text-primary">
            <span className="text-gradient">ReShare</span>
          </Link>
        </div>

        <div className="flex items-center gap-3">
          {user ? (
            <>
              <Button variant="ghost" size="icon" className="rounded-full" asChild>
                <Link href="/search">
                  <Search className="h-5 w-5" />
                </Link>
              </Button>

              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="sm" className="rounded-full">
                    <Avatar className="h-8 w-8">
                      <AvatarImage
                        src={user?.avatarUrl || undefined}
                        alt={user?.username || 'User'}
                      />
                      <AvatarFallback>{(user?.username?.[0] || 'U').toUpperCase()}</AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel>My Account</DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link href={`/profile/${user.id}`} className="w-full cursor-pointer">
                      Profile
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/dashboard" className="w-full cursor-pointer">
                      Dashboard
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/settings" className="w-full cursor-pointer">
                      Settings
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem className="cursor-pointer text-destructive" onClick={logout}>
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </>
          ) : (
            <Button variant="outline" size="sm" asChild onClick={() => signIn('keycloak')}>
              Sign In
            </Button>
          )}
        </div>
      </div>
    </header>
  )
}

export default Navbar
