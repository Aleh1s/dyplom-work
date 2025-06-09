import type { JWT } from 'next-auth/jwt'
import { toast } from 'sonner'

const refreshAccessToken = async (token: JWT): Promise<JWT> => {
  try {
    const response = await fetch(`${process.env.KEYCLOAK_ISSUER}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        grant_type: 'refresh_token',
        client_id: process.env.KEYCLOAK_CLIENT_ID!,
        client_secret: process.env.KEYCLOAK_CLIENT_SECRET!,
        refresh_token: token.refreshToken ?? ''
      })
    })

    const refreshedTokens = await response.json()

    return {
      ...token,
      accessToken: refreshedTokens.access_token,
      expiresAt: Math.floor(Date.now() / 1000) + refreshedTokens.expires_in,
      refreshToken: refreshedTokens.refresh_token ?? token.refreshToken
    }
  } catch {
    toast.error('Refresh access token error')

    return { ...token, error: 'RefreshAccessTokenError' }
  }
}

export default refreshAccessToken
