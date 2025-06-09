import { signOut } from 'next-auth/react'
import useAuthStore from '@/_shared/state/useAuthStore'

const logout = async () => {
  useAuthStore.setState({ user: null })
  await signOut()
}

export default logout
