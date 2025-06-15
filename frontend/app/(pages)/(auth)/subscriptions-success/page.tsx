'use client'

import { subscriptionsApi } from "@/_shared/api/subscriptions";
import { Loader2 } from "lucide-react";
import { redirect, useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import { toast } from "sonner";

const SubscriptionsSuccessPage = () => {
    const router = useRouter();
    const searchParams = useSearchParams();

    const sessionId = searchParams.get('session_id');

    const handleSubscriptionsPayment = async (sessionId: string) => {
        try {
            const subscription = await subscriptionsApi.handleSubscriptionsPayment(sessionId);

            if (!subscription) {
                toast.error('Something went wrong');
            } else {
                router.push(`/profile/${subscription.subscribedOnId}`);
            }
        } catch (error: any) {
            toast.error('Something went wrong');
        }
    }

    useEffect(() => {
        if (!sessionId) {
            redirect('/dashboard');
        } else {
            handleSubscriptionsPayment(sessionId);
        }
    }, [sessionId])

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-4">
            <h1 className="text-3xl font-bold text-center mb-4">
                Payment Successful!
            </h1>
            <p className="text-text-secondary text-center mb-8">
                Thank you for your subscription. You will be redirected shortly...
            </p>
            <div className="animate-spin">
                <Loader2 className="h-8 w-8" />
            </div>
        </div>
    );
}

export default SubscriptionsSuccessPage;