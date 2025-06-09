'use client'

import { FilePlus } from 'lucide-react'
import { useRouter } from 'next/navigation'
import React, { useCallback } from 'react'
import { useDropzone } from 'react-dropzone'
import { Controller, useForm } from 'react-hook-form'
import { toast } from 'sonner'
import MainLayout from '@/_components/layout/MainLayout'
import { contentApi } from '@/_shared/api/content'
import { mediaApi } from '@/_shared/api/media'
import { Button } from '@/_shared/ui/button'
import { Textarea } from '@/_shared/ui/textarea'

interface FormData {
  description: string
  file: File | null
}

const NewContent = () => {
  const router = useRouter()

  const {
    control,
    handleSubmit,
    setValue,
    watch,
    formState: { isValid, isSubmitting }
  } = useForm<FormData>({
    defaultValues: {
      description: '',
      file: null
    }
  })

  const onDrop = useCallback((acceptedFiles: File[]) => {
    setValue('file', acceptedFiles[0])
  }, [setValue])

  const { getRootProps, getInputProps } = useDropzone({ onDrop })

  const onSubmit = handleSubmit(async data => {
    if (!data.file) {
      toast.error('File is required')

      return
    }

    const { mediaUrl, previewUrl, safePreviewUrl } = await mediaApi.uploadSafe(data.file)

    const content = await contentApi.create({
      description: data.description,
      mediaUrl,
      previewUrl,
      safePreviewUrl
    })

    toast.success('Content created successfully')
    router.push(`/my-gallery?contentId=${content.id}`)
  })

  const file = watch('file')

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold">Create New Content</h1>
        </div>

        <div className="space-y-6">
          <div className="p-6 rounded-2xl bg-background-surface border border-white/10">
            <form className="space-y-6">
              <div className="space-y-2">
                <label className="text-sm text-muted-foreground">Description</label>
                <Controller
                  control={control}
                  name="description"
                  rules={{
                    required: 'Description is required',
                    minLength: {
                      value: 3,
                      message: 'Description must be at least 3 characters long'
                    },
                    maxLength: {
                      value: 100,
                      message: 'Description must be less than 250 characters long'
                    }
                  }}
                  render={({ field, fieldState: { error } }) => (
                    <div className="space-y-2">
                      <Textarea
                        placeholder="Describe your content..."
                        className="min-h-[120px]"
                        {...field}
                      />
                      {error && <p className="text-red-500 text-sm">{error.message}</p>}
                    </div>
                  )}
                />
              </div>

              <div
                {...getRootProps()}
                className="border-2 border-dashed border-white/10 rounded-xl p-8 text-center"
              >
                <FilePlus className="w-12 h-12 mx-auto mb-4 text-muted-foreground" />
                <p className="text-muted-foreground">
                  {file ? file.name : 'Drag and drop your content here, or click to browse'}
                </p>
                <input {...getInputProps()} />
              </div>

              <Button
                className="w-full bg-premium-gradient"
                onClick={onSubmit}
                disabled={!file || !isValid || isSubmitting}
              >
                {isSubmitting ? 'Creating...' : 'Create Content'}
              </Button>
            </form>
          </div>
        </div>
      </div>
    </MainLayout>
  )
}

export default NewContent
