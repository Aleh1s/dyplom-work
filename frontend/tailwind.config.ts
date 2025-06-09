import type { Config } from 'tailwindcss'
import animate from 'tailwindcss-animate'

export default {
  darkMode: ['class'],
  content: ['./app/**/*.{ts,tsx}'],
  prefix: '',
  theme: {
    container: {
      center: true,
      padding: '2rem',
      screens: {
        '2xl': '1400px'
      }
    },
    extend: {
      colors: {
        'background-surface': '#1A1A1A',
        'primary-accent': '#6366F1',
        'secondary-accent': '#22D3EE',
        'text-primary': '#F5F5F5',
        'text-secondary': '#9CA3AF',
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: {
          DEFAULT: '#0D0D0D',
          surface: '#1A1A1A'
        },
        foreground: '#F5F5F5',
        primary: {
          DEFAULT: '#6366F1',
          foreground: '#F5F5F5'
        },
        secondary: {
          DEFAULT: '#22D3EE',
          foreground: '#F5F5F5'
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))'
        },
        muted: {
          DEFAULT: '#2D2D2D',
          foreground: '#9CA3AF'
        },
        accent: {
          DEFAULT: '#2D2D2D',
          foreground: '#F5F5F5'
        },
        popover: {
          DEFAULT: '#1A1A1A',
          foreground: '#F5F5F5'
        },
        card: {
          DEFAULT: '#1A1A1A',
          foreground: '#F5F5F5'
        }
      },
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)',
        '2xl': '1rem'
      },
      keyframes: {
        'accordion-down': {
          from: { height: '0' },
          to: { height: 'var(--radix-accordion-content-height)' }
        },
        'accordion-up': {
          from: { height: 'var(--radix-accordion-content-height)' },
          to: { height: '0' }
        },
        shimmer: {
          '100%': {
            transform: 'translateX(100%)'
          }
        },
        fadeIn: {
          from: { opacity: '0', transform: 'translateY(10px)' },
          to: { opacity: '1', transform: 'translateY(0)' }
        }
      },
      animation: {
        'accordion-down': 'accordion-down 0.2s ease-out',
        'accordion-up': 'accordion-up 0.2s ease-out',
        shimmer: 'shimmer 2s infinite',
        fadeIn: 'fadeIn 0.5s ease-out forwards'
      },
      boxShadow: {
        glow: '0 0 20px rgba(99, 102, 241, 0.3)',
        subtle: '0 4px 12px rgba(0, 0, 0, 0.1)'
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-conic': 'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
        'premium-gradient': 'linear-gradient(135deg, #6366F1 0%, #22D3EE 100%)'
      }
    }
  },
  plugins: [animate]
} satisfies Config
