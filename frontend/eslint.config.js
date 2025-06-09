import eslint from '@eslint/js'
import tseslint from 'typescript-eslint'
import prettier from 'eslint-config-prettier'
import prettierPlugin from 'eslint-plugin-prettier'
import reactPlugin from 'eslint-plugin-react'
import reactHooksPlugin from 'eslint-plugin-react-hooks'
import importPlugin from 'eslint-plugin-import'
import unusedImportsPlugin from 'eslint-plugin-unused-imports'
import nextPlugin from '@next/eslint-plugin-next'
import globals from 'globals'

export default tseslint.config(
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  prettier,
  {
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        project: ['./tsconfig.json'],
        ecmaVersion: 'latest',
        sourceType: 'module',
        ecmaFeatures: {
          jsx: true
        }
      },
      globals: {
        ...globals.browser,
        ...globals.es2021,
        ...globals.node
      }
    },
    plugins: {
      '@typescript-eslint': tseslint.plugin,
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
      import: importPlugin,
      'unused-imports': unusedImportsPlugin,
      prettier: prettierPlugin,
      next: nextPlugin
    },
    settings: {
      react: {
        version: 'detect'
      }
    },
    rules: {
      camelcase: 'off',
      'no-console': 'warn',
      'no-floating-decimal': 'error',
      'no-sequences': 'error',
      curly: 'off',
      'no-lonely-if': 'error',
      'one-var-declaration-per-line': 'error',
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',
      '@typescript-eslint/consistent-type-exports': 'error',
      '@typescript-eslint/consistent-type-imports': 'error',
      'func-style': 'error',
      'no-debugger': 'warn',
      'no-empty': 'error',
      semi: ['warn', 'never'],
      'newline-before-return': 'error',
      'newline-after-var': 'error',
      'space-in-parens': 'error',
      'react/jsx-wrap-multilines': 'off',
      'react/no-unescaped-entities': 0,
      'no-unused-vars': 'off',
      quotes: [
        'error',
        'single',
        {
          allowTemplateLiterals: false,
          avoidEscape: true
        }
      ],
      'array-callback-return': 'off',
      'prettier/prettier': [
        'warn',
        {
          "semi": false,
          "singleQuote": true,
          "tabWidth": 2,
          "trailingComma": "none",
          "printWidth": 100,
          "bracketSpacing": true,
          "arrowParens": "avoid"
        }
      ],
      'import/order': [
        'error',
        {
          groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index'],
          pathGroups: [
            {
              pattern: '@shared/**',
              group: 'internal',
              position: 'after'
            },
            {
              pattern: '@entities/**',
              group: 'internal',
              position: 'after'
            },
            {
              pattern: '@features/**',
              group: 'internal',
              position: 'after'
            },
            {
              pattern: '@widgets/**',
              group: 'internal',
              position: 'after'
            },
            {
              pattern: '@screens/**',
              group: 'internal',
              position: 'after'
            },
            {
              pattern: '@app/**',
              group: 'internal',
              position: 'after'
            }
          ],
          pathGroupsExcludedImportTypes: ['builtin'],
          'newlines-between': 'never',
          alphabetize: {
            order: 'asc',
            caseInsensitive: true
          }
        }
      ],
      'unused-imports/no-unused-imports': 'warn',
      'unused-imports/no-unused-vars': [
        'warn',
        {
          vars: 'all',
          varsIgnorePattern: '^_',
          args: 'after-used',
          argsIgnorePattern: '^_'
        }
      ]
    },
    ignores: ['dist/', 'node_modules/', 'metro.config.js']
  }
)
