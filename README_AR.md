# TubeDub IQ 1.0.0 — نسخة تأسيسية

هذا مشروع أندرويد جديد ومستقل مستوحى من **وظائف** TranslateTube 1.0.4، وليس نسخة معدّلة من APK الأصلي أو استرجاعاً لمصدره.

## الموجود في النسخة الحالية

- استخراج مسارات الترجمة المتاحة من رابط YouTube عام، مع تفضيل العربية إن وجدت.
- اختيار الترجمة التلقائية أو اليدوية عند توفرها (يختار التطبيق العربية أولاً، وإلا أول مسار متاح).
- استيراد SRT / VTT / TXT وتطبيع تنسيق SRT الأساسي.
- ترجمة النص إلى لهجة عراقية عبر Gemini Interactions API، مع تقسيم النص الطويل إلى دفعات.
- توليد MP3 عبر Fish Audio S2.1 Pro Free عند إدخال مفتاح صالح، مع Voice ID اختياري.
- نطق محلي تجريبي عبر Android TextToSpeech.
- تصدير الترجمة الناتجة إلى ملف SRT.
- حفظ مفاتيح API مشفّرة باستخدام Android Keystore.

## حدود النسخة التأسيسية

- استخراج YouTube يعتمد على مسارات الترجمة المتاحة علناً؛ بعض الفيديوهات قد تفشل بسبب قيود المنطقة أو تسجيل الدخول أو تحقق YouTube أو عدم وجود ترجمة.
- لا تتجاوز هذه النسخة حماية CAPTCHA أو القيود الخاصة أو DRM.
- الدبلجة عبر Fish Audio في هذه النسخة محدودة إلى 3500 حرف لكل طلب، ولا تدمج الصوت الناتج تلقائياً مع مسار الفيديو الأصلي.
- فصل الكلام عن الموسيقى، مزامنة المقاطع الصوتية مع توقيت كل جملة، اختيار أصوات متعددة للشخصيات، وسجل المشاريع ما زالت مراحل لاحقة.
- استخدام خدمات Gemini وFish Audio يحتاج مفاتيح API خاصة بالمستخدم وقد يخضع لحدود أو تكاليف مقدم الخدمة.

## البناء في Termux

المتطلبات: Java 17 أو 21، Gradle 8.11.1 أو أحدث، Android SDK مع Platform 36 وBuild Tools مناسبة.

```bash
cd ~/TubeDubIQ-1.0.0
gradle --version
gradle assembleDebug
```

الناتج المتوقع:

```text
app/build/outputs/apk/debug/app-debug.apk
```

سكريبت البناء يحاول تلقائياً استخدام Gradle Wrapper من مشروع VOT الحالي إذا كان موجوداً بنفس المسار المعروف. بعد نجاح البناء ينسخ APK إلى مجلد Download إذا كان تخزين Termux مفعّلاً.

## مفاتيح الخدمات

- Gemini: https://aistudio.google.com/app/api-keys
- Fish Audio: https://fish.audio/app/api-keys/

لا ترسل مفاتيحك لأي شخص ولا تضعها داخل ملفات المصدر قبل مشاركة المشروع.

## مراجع الواجهات المستخدمة

- Gemini Interactions API: https://ai.google.dev/gemini-api/docs/get-started
- Fish Audio TTS: https://fish.audio/developers/
- توافق Android Gradle Plugin 8.10: https://developer.android.com/build/releases/agp-8-10-0-release-notes

## البناء التلقائي على GitHub

يوجد Workflow في `.github/workflows/android.yml` يعمل عند كل Push إلى `main`، ويمكن تشغيله يدوياً من تبويب Actions. عند نجاح البناء يرفع ملف APK باسم `TubeDubIQ-debug-apk` كـ Artifact لمدة 14 يوماً. هذا APK تجريبي من نوع Debug وليس إصدار Release موقّعاً للنشر.
