import ky from 'ky'
import { getSession } from 'next-auth/react'

const api = ky.create({
  prefixUrl: process.env.NEXT_PUBLIC_BACKEND_URL,
  hooks: {
    beforeRequest: [
      async request => {
        const session = await getSession()

        if (session?.accessToken) {
          request.headers.set('Authorization', `Bearer ${session.accessToken}`)
        }
      }
    ]
  }
})

export default api
