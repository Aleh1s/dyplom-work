import { type AuthOptions } from 'next-auth'
import KeycloakProvider from 'next-auth/providers/keycloak'
import refreshAccessToken from '@/_shared/lib/refreshAccessToken'

export const authOptions: AuthOptions = {
  providers: [
    KeycloakProvider({
      clientId: process.env.KEYCLOAK_CLIENT_ID!,
      clientSecret: process.env.KEYCLOAK_CLIENT_SECRET!,
      issuer: process.env.KEYCLOAK_ISSUER!,
      authorization: {
        params: {
          scope: 'openid email profile'
        }
      }
    })
  ],
  callbacks: {
    async jwt({ token, account }) {
      if (account) {
        token.accessToken = account.access_token
        token.refreshToken = account.refresh_token
        token.expiresAt = account.expires_at

        return token
      }

      if (Date.now() / 1000 < token.expiresAt!) {
        return token
      }

      return await refreshAccessToken(token)
    },
    async session({ session, token }) {
      if (token.accessToken) {
        session.accessToken = token.accessToken
      }

      return session
    }
  }
}
