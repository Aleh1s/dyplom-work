import { getServerSession } from 'next-auth'
import AuthInitializer from './_shared/initializer/AuthInitializer'
import getInitialUserData from './_shared/lib/getInitialUserData'
import CustomSessionProvider from './_shared/provider/CustomSessionProvider'
import { authOptions } from './lib/auth'

import '@/_shared/styles/globals.css'
import '@/_shared/styles/root.css'

const RootLayout = async ({ children }: { children: React.ReactNode }) => {
  const session = await getServerSession(authOptions)
  const data = await getInitialUserData()

  return (
    <html lang="en">
      <body>
        <div id="root">
          <AuthInitializer user={data.user} subscriptionPlan={data.subscriptionPlan} />
          <CustomSessionProvider session={session}>{children}</CustomSessionProvider>
        </div>
      </body>
    </html>
  )
}

export default RootLayout
