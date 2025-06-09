'use client'

import { Settings as SettingsIcon } from 'lucide-react'
import React from 'react'
import { Controller, useForm } from 'react-hook-form'
import { toast } from 'sonner'
import MainLayout from '@/_components/layout/MainLayout'
import type { SubscriptionTypeType } from '@/_shared/api/types'
import useAuthStore from '@/_shared/state/useAuthStore'
import { Button } from '@/_shared/ui/button'
import { Card } from '@/_shared/ui/card'
import { Input } from '@/_shared/ui/input'
import { Select, SelectContent, SelectItem, SelectValue, SelectTrigger } from '@/_shared/ui/select'

interface FormData {
  type: SubscriptionTypeType
  price: number
}

const Settings = () => {
  const subscriptionPlan = useAuthStore(state => state.subscriptionPlan)
  const updateSubscriptionPlan = useAuthStore(state => state.updateSubscriptionPlan)

  const { control, handleSubmit } = useForm<FormData>({
    defaultValues: {
      type: subscriptionPlan?.type,
      price: subscriptionPlan?.price
    }
  })

  const onSubmit = handleSubmit(data => {
    updateSubscriptionPlan(data)
      .then(() => toast.success('Subscription plan updated'))
      .catch(() => {
        toast.error('Failed to update subscription plan')
      })
  })

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center gap-3 mb-8">
          <SettingsIcon className="w-6 h-6" />
          <h1 className="text-2xl font-bold">Settings</h1>
        </div>

        <Card className="p-6 bg-background-surface border-white/10">
          <h2 className="text-lg font-medium mb-4">Subscription Settings</h2>
          <div className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm text-muted-foreground">Subscription Plan</label>
              <Controller
                control={control}
                name="type"
                render={({ field }) => (
                  <Select onValueChange={field.onChange} value={field.value}>
                    <SelectTrigger>
                      <SelectValue placeholder="Select a plan" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="FREE">FREE</SelectItem>
                      <SelectItem value="PREMIUM">PREMIUM</SelectItem>
                    </SelectContent>
                  </Select>
                )}
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-muted-foreground">Price</label>
              <Controller
                control={control}
                name="price"
                render={({ field }) => <Input type="number" placeholder="100" {...field} />}
              />
            </div>
          </div>

          <Button type="submit" className="mt-4" size="sm" onClick={onSubmit}>
            Update
          </Button>
        </Card>
      </div>
    </MainLayout>
  )
}

export default Settings
