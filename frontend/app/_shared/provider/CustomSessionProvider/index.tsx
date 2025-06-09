'use client'

import type { Session } from 'next-auth'
import { SessionProvider } from 'next-auth/react'
import type { FC, PropsWithChildren } from 'react'

interface Props extends PropsWithChildren {
  session: Session | null
}

const CustomSessionProvider: FC<Props> = ({ children, session }) => {
  return <SessionProvider session={session}>{children}</SessionProvider>
}

export default CustomSessionProvider
