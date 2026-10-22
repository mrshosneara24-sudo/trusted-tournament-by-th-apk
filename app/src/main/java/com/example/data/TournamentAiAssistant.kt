package com.example.data

import com.example.model.UserProfile
import java.util.Locale

data class AiResponse(
    val text: String,
    val actionType: String? = null,
    val actionLabel: String? = null
)

object TournamentAiAssistant {

    val quickQuestions = listOf(
        "⚡ টাকা ডিপোজিট / TrxID সমস্যা",
        "🔑 রুম আইডি ও পাসওয়ার্ড কখন পাব?",
        "💳 উইথড্র / ক্যাশআউট করব কীভাবে?",
        "🎟️ টুর্নামেন্টে স্লট কীভাবে বুক করব?",
        "🏆 V-ব্যাজ ও প্রো ব্যাজ কীভাবে পাব?",
        "🎁 সাপ্তাহিক ফ্রি লাকি স্পিন কীভাবে পাব?",
        "🔥 দৈনিক ৭ দিনের স্ট্রিক বোনাস কী?",
        "⚡ ফ্রি ফায়ার প্রো আইডি কার্ড ও শেয়ার",
        "📲 বন্ধুদের সাথে অ্যাপ শেয়ার ও APK ডাউনলোড",
        "🛡️ আইডি ব্যান বা সমস্যা সমাধান"
    )

    fun answerQuery(
        query: String,
        userProfile: UserProfile,
        walletBalance: Int,
        activeBookingsCount: Int,
        officialBkash: String
    ): AiResponse {
        val q = query.lowercase(Locale.ROOT).trim()

        return when {
            // Greetings
            q.contains("সালাম") || q.contains("salam") || q.contains("হ্যালো") || q.contains("hello") || q.contains("hi") || q.contains("হাই") -> {
                AiResponse(
                    text = "ওয়ালাইকুম আসসালাম! আমি TRUSTED TOURNAMENT এর স্মার্ট AI সহকারী 🤖। আপনার যে কোনো সমস্যা (যেমন: ডিপোজিট, TrxID, রুম কোড, উইথড্র, স্লট বুকিং ইত্যাদি) মুহূর্তেই সমাধান করে দিতে পারি। নিচে থেকে আপনার সমস্যাটি বেছে নিন অথবা সরাসরি লিখে পাঠান!",
                    actionType = null,
                    actionLabel = null
                )
            }

            // TrxID / Transaction ID Help
            q.contains("trxid") || q.contains("trx id") || q.contains("ট্রানজেকশন") || q.contains("ট্রানজ্যাকশন") || q.contains("transaction") -> {
                AiResponse(
                    text = "🔍 **TrxID (ট্রানজেকশন আইডি) সম্পর্কিত সমাধান:**\n\n" +
                            "১. বিকাশ/নগদে সেন্ড মানি করার পর তাদের ফিরতি SMS এ ৮-১০ অক্ষরের একটি ইউনিক কোড থাকে (যেমন: `9J3K5L7M81`)। এটাই TrxID।\n" +
                            "২. বিকাশ অ্যাপের 'ইনবক্স' -> 'লেনদেন' অপশনে গেলেও TrxID পাওয়া যায়।\n" +
                            "৩. স্লট বুকিং বা ওয়ালেট রিচার্জ করার সময় এই TrxID টি সঠিকভাবে কপি করে পেস্ট করুন।\n" +
                            "৪. যদি কোনো কারণে ভুল TrxID দিয়ে ফেলেন, দ্রুত অফিসিয়াল হেল্পলাইনে ($officialBkash) যোগাযোগ করুন।",
                    actionType = "ACTION_OPEN_WALLET",
                    actionLabel = "💳 ওয়ালেট দেখুন"
                )
            }

            // Deposit / Add Money Problem
            q.contains("ডিপোজিট") || q.contains("deposit") || q.contains("টাকা যোগ") || q.contains("add money") || q.contains("টাকা আসে নাই") || q.contains("ব্যালেন্স") -> {
                AiResponse(
                    text = "💰 **টাকা ডিপোজিট / ব্যালেন্স সমস্যার সমাধান:**\n\n" +
                            "• অফিসিয়াল বিকাশ নাম্বার: `$officialBkash` (Personal)।\n" +
                            "• টাকা পাঠানোর পর TrxID সাবমিট করলে স্বয়ংক্রিয়ভাবে ব্যালেন্সে যোগ হয়ে যায়। আপনার বর্তমান ব্যালেন্স: ৳$walletBalance।\n" +
                            "• যদি TrxID দেওয়ার পরেও ব্যালেন্স আপডেট না হয়, ওয়ালেট স্ক্রিনে রিফ্রেশ করুন অথবা হেল্পলাইনে ট্রানজ্যাকশন স্ক্রিনশট পাঠিয়ে ১ মিনিটে ঠিক করে নিন।",
                    actionType = "ACTION_OPEN_WALLET",
                    actionLabel = "💳 ওয়ালেট রিচার্জ করুন"
                )
            }

            // Room ID & Password
            q.contains("রুম") || q.contains("room") || q.contains("পাসওয়ার্ড") || q.contains("password") || q.contains("কাস্টম") || q.contains("custom") -> {
                AiResponse(
                    text = "🔑 **রুম আইডি ও পাসওয়ার্ড পাওয়ার নিয়ম:**\n\n" +
                            "• ম্যাচ শুরু হওয়ার ঠিক **১৫ মিনিট আগে** অ্যাপের 'আমার ম্যাচসমূহ' (My Matches) অপশনে স্বয়ংক্রিয়ভাবে রুম আইডি এবং পাসওয়ার্ড উন্মুক্ত হয়ে যায়।\n" +
                            "• আপনি ফ্রি ফায়ার গেমে গিয়ে `Custom` -> `Search Room ID` দিয়ে রুমে প্রবেশ করবেন এবং পাসওয়ার্ড দিবেন।\n" +
                            "• **সতর্কতা:** বুকিং করার সময় যে স্লট নাম্বার নিয়েছেন, কাস্টম রুমে ঠিক সেই স্লটেই বসবেন। অন্য স্লটে বসলে কিক খেতে পারেন।",
                    actionType = "ACTION_OPEN_MY_MATCHES",
                    actionLabel = "🎮 আমার ম্যাচ ও রুম কোড দেখুন"
                )
            }

            // Withdraw / Cashout
            q.contains("উইথড্র") || q.contains("withdraw") || q.contains("ক্যাশআউট") || q.contains("cashout") || q.contains("টাকা তুলব") || q.contains("টাকা বের") -> {
                AiResponse(
                    text = "💳 **টাকা উইথড্র / ক্যাশআউট করার নিয়মাবলী:**\n\n" +
                            "• **উইথড্র লিমিট:** সর্বনিম্ন **৳৫০** এবং সর্বোচ্চ **৳২০০** টাকা পর্যন্ত ক্যাশআউট করা যাবে।\n" +
                            "• ওয়ালেট স্ক্রিনে গিয়ে 'ক্যাশআউট' বাটনে চাপুন।\n" +
                            "• আপনার বিকাশ পার্সোনাল নম্বর ও টাকার পরিমাণ (৫০ থেকে ২০০ টাকা) লিখুন।\n" +
                            "• এডমিন ভেরিফাই করে ৫ থেকে ৩০ মিনিটের মধ্যে সরাসরি আপনার বিকাশ নম্বরে টাকা পাঠিয়ে দিবে।",
                    actionType = "ACTION_OPEN_WALLET",
                    actionLabel = "💸 ক্যাশআউট রিকোয়েস্ট দিন"
                )
            }

            // Slot Booking
            q.contains("স্লট") || q.contains("slot") || q.contains("বুকিং") || q.contains("booking") || q.contains("ম্যাচে ঢুকব") || q.contains("খেলব") -> {
                AiResponse(
                    text = "🎟️ **স্লট বুকিং ও ম্যাচে অংশ নেওয়ার সহজ ধাপ:**\n\n" +
                            "১. 'হোম' স্ক্রিনে গিয়ে আপনার পছন্দের ম্যাচ (Solo / Duo / Squad) সিলেক্ট করুন।\n" +
                            "২. 'স্লট বুক করুন' চাপুন এবং রুমের খালি স্লট (১ থেকে ৪৮) বেছে নিন।\n" +
                            "৩. আপনার Free Fire IGN (নাম) এবং UID দিন।\n" +
                            "৪. ওয়ালেট ব্যালেন্স অথবা বিকাশ দিয়ে এন্ট্রি ফি পেমেন্ট করে TrxID কনফার্ম করুন। আপনার স্লট সাথে সাথে বুক হয়ে যাবে!",
                    actionType = "ACTION_OPEN_MATCHES",
                    actionLabel = "🏆 টুর্নামেন্ট ম্যাচ দেখুন"
                )
            }

            // V-Badge & Pro Badge
            q.contains("v badge") || q.contains("v-badge") || q.contains("ভি ব্যাজ") || q.contains("pro badge") || q.contains("ব্যাজ") || q.contains("badge") -> {
                AiResponse(
                    text = "🏅 **V-Badge এবং PRO Badge পাওয়ার নিয়মাবলী:**\n\n" +
                            "• Ⓥ **গোল্ডেন V-ব্যাজ:** প্ল্যাটফর্মের সর্বোচ্চ ডিপোজিটকারী বা ভিআইপি এলিট প্লেয়ারদের এই অফিসিয়াল ফ্রি ফায়ার স্টাইল V-ব্যাজ দেওয়া হয়।\n" +
                            "• ⚡ **PRO ব্যাজ:** যারা বেশি বেশি টুর্নামেন্ট খেলে, সর্বোচ্চ কিল করে এবং লিডারবোর্ডের শীর্ষে থাকে তাদের প্রো ব্যাজ দেওয়া হয়।\n" +
                            "• 👑 **ওনার / এডমিন ব্যাজ:** অফিশিয়াল সিস্টেম কন্ট্রোলারদের জন্য সংরক্ষিত।\n" +
                            "আপনার অ্যাকাউন্টে ব্যাজ সক্রিয় হলে প্রোফাইলে এবং লিডারবোর্ডে সোনালী দ্যুতি সহ প্রদর্শিত হবে!",
                    actionType = "ACTION_OPEN_ACCOUNT",
                    actionLabel = "👤 আমার প্রোফাইল দেখুন"
                )
            }

            // Weekly Free Lucky Spin
            q.contains("স্পিন") || q.contains("spin") || q.contains("লাকি") || q.contains("ফ্রি টাকা") || q.contains("উপহার") -> {
                AiResponse(
                    text = "🎰 **সাপ্তাহিক ফ্রি লাকি স্পিন গাইড:**\n\n" +
                            "• প্রত্যেক প্লেয়ার প্রতি সপ্তাহে ১টি সম্পূর্ণ ফ্রি স্পিন সুযোগ পায়!\n" +
                            "• স্পিন ঘুরিয়ে ৯৯% ক্ষেত্রে নিশ্চিতভাবে **৳৩, ৳৪ অথবা ৳৫** নগদ টাকা ওয়ালেটে জমা হয়।\n" +
                            "• সর্বোচ্চ **৳৩০ মেগা জ্যাকপট** জেতা অনেক অনেক অনেক কঠিন ও চরম দুর্লভ (মাত্র ০.০৫% বা ২০০০ জনে মাত্র ১ জন)!\n" +
                            "• স্পিন করলেই নগদ টাকা সরাসরি ওয়ালেটে অ্যাড হয়ে যায় এবং ক্যাশআউট করা যায়।",
                    actionType = "ACTION_OPEN_SPIN",
                    actionLabel = "🎁 লাকি স্পিন খুলুন"
                )
            }

            // Daily Login Streak
            q.contains("স্ট্রিক") || q.contains("streak") || q.contains("দৈনিক") || q.contains("ডেইলি") || q.contains("৭ দিন") -> {
                AiResponse(
                    text = "🔥 **দৈনিক ৭ দিনের লগইন স্ট্রিক বোনাস:**\n\n" +
                            "• প্রতিদিন একবার অ্যাপ ওপেন করলেই বিনামূল্যে নগদ টাকা ক্লেইম করতে পারবেন!\n" +
                            "• দিন ১: ৳১, দিন ২: ৳২, দিন ৩: ৳৩, দিন ৪: ৳৫, দিন ৫: ৳৭, দিন ৬: ৳১০ এবং ৭ম দিন মেগা বোনাস: ৳১৫!\n" +
                            "• টানা ৭ দিন লগইন বজায় রেখে সর্বোচ্চ বোনাস জিতে নিন। হোম স্ক্রিনের উপরে ফায়ার আইকন বা স্ট্রিক কার্ডে ট্যাপ করুন।",
                    actionType = "ACTION_OPEN_STREAK",
                    actionLabel = "🔥 আজকের স্ট্রিক বোনাস নিন"
                )
            }

            // Free Fire Esports Card / Loadout
            q.contains("কার্ড") || q.contains("card") || q.contains("লোডআউট") || q.contains("loadout") || q.contains("গান") || q.contains("ক্যারেক্টার") || q.contains("গিল্ড") -> {
                AiResponse(
                    text = "⚡ **ফ্রি ফায়ার প্রো ব্যাটল কার্ড ও লোডআউট:**\n\n" +
                            "• আপনার প্রোফাইল স্ক্রিনে একটি ডায়নামিক Free Fire Esports ID Card যুক্ত রয়েছে।\n" +
                            "• সেখানে আপনার গেম IGN, UID, গিল্ড ট্যাগ, প্রিয় গান স্কিন, ক্যারেক্টার, হেডশট রেট এবং K/D প্রদর্শিত হয়।\n" +
                            "• 'প্লেয়ার আইডি কার্ড কপি ও শেয়ার করুন' বাটনে চাপ দিয়ে বন্ধুদের মেসেঞ্জারে বা হোয়াটসঅ্যাপে শেয়ার করতে পারবেন!",
                    actionType = "ACTION_OPEN_ACCOUNT",
                    actionLabel = "👤 আমার প্লেয়ার কার্ড দেখুন"
                )
            }

            // Share / APK Download
            q.contains("apk") || q.contains("ডাউনলোড") || q.contains("শেয়ার") || q.contains("share") || q.contains("বন্ধু") || q.contains("friends") || q.contains("install") -> {
                AiResponse(
                    text = "📲 **বন্ধুদের সাথে অ্যাপ শেয়ার ও APK ডাউনলোড করার পদ্ধতি:**\n\n" +
                            "১. Google AI Studio এর স্ক্রিনের উপরে ডানপাশে Settings বা Export অপশন পাবেন।\n" +
                            "২. সেখানে 'Download APK' বা 'Export Project' এ চাপ দিয়ে আপনার মোবাইলে `.apk` ফাইলটি নামিয়ে নিন।\n" +
                            "৩. এরপর WhatsApp, Messenger, Bluetooth বা ShareIt দিয়ে আপনার বন্ধুদের ফাইলটি পাঠিয়ে দিন।\n" +
                            "৪. বন্ধুরা শুধু 'Install' এ ক্লিক করলেই অ্যাপ ওপেন হয়ে যাবে এবং আপনারা একসাথে একই ম্যাচে স্কোয়াড বা সোলো খেলতে পারবেন!",
                    actionType = null,
                    actionLabel = null
                )
            }

            // Banned / Cheat / Hack / Rules
            q.contains("ব্যান") || q.contains("ban") || q.contains("হ্যাকার") || q.contains("hack") || q.contains("চিটার") || q.contains("cheat") || q.contains("নিয়ম") -> {
                AiResponse(
                    text = "🛡️ **ফেয়ার প্লে ও অ্যান্টি-চিট নীতি:**\n\n" +
                            "• কোনো প্রকার হ্যাক, কনফিগ ফাইল, স্ক্রিপ্ট বা টিম-আপ সম্পূর্ণ নিষিদ্ধ। প্রমাণিত হলে স্থায়ীভাবে ডিভাইস ও আইডি ব্যান করা হয়।\n" +
                            "• যদি আপনি ভুলবশত ব্যান হয়ে থাকেন, তবে ওনার বা এডমিন প্যানেলে রিভিউয়ের জন্য অফিসিয়াল নাম্বারে ($officialBkash) প্রমাণসহ মেসেজ দিন।",
                    actionType = "ACTION_OPEN_RULES",
                    actionLabel = "📜 টুর্নামেন্ট নিয়মাবলী পড়ুন"
                )
            }

            // Admin / Helpline / Contact
            q.contains("এডমিন") || q.contains("admin") || q.contains("মালিক") || q.contains("owner") || q.contains("নাম্বার") || q.contains("helpline") || q.contains("যোগাযোগ") -> {
                AiResponse(
                    text = "📞 **অফিসিয়াল এডমিন ও হেল্পলাইন তথ্য:**\n\n" +
                            "• হেল্পলাইন ও বিকাশ (পার্সোনাল): `$officialBkash`\n" +
                            "• প্রতিদিন সকাল ৯:০০ টা থেকে রাত ১১:০০ টা পর্যন্ত লাইভ সাপোর্ট টিম সক্রিয় থাকে।\n" +
                            "• লাইভ এডমিন ট্যাবে সরাসরি মেসেজ পাঠিয়েও যে কোনো সমস্যার দ্রুত সমাধান পেতে পারেন।",
                    actionType = "ACTION_CALL_ADMIN",
                    actionLabel = "📞 এডমিন হেল্পলাইন: $officialBkash"
                )
            }

            // Thank you
            q.contains("ধন্যবাদ") || q.contains("thanks") || q.contains("থ্যাঙ্ক") -> {
                AiResponse(
                    text = "আপনাকেও অসংখ্য ধন্যবাদ! 😊 TRUSTED TOURNAMENT এ ভালো খেলুন, বুইয়া নিন এবং প্রতিদিন নগদ পুরস্কার জিতে নিন। যেকোনো প্রয়োজনে আমি সবসময় আপনার পাশে আছি!",
                    actionType = null,
                    actionLabel = null
                )
            }

            // Default Fallback with Smart Understanding
            else -> {
                AiResponse(
                    text = "🤖 আপনার জিজ্ঞাসাটি পেয়েছি। ফ্রি ফায়ার টুর্নামেন্ট, বিকাশ TrxID, রুম আইডি, ক্যাশআউট বা স্লট বুকিং সংক্রান্ত যেকোনো প্রশ্ন আমাকে করতে পারেন।\n\n" +
                            "যদি বিশেষ কোনো জটিল সমস্যা হয়ে থাকে, আপনি আমাদের লাইভ এডমিন টিমের সাথে সরাসরি চ্যাট করতে পারেন অথবা নিচের দ্রুত অপশনগুলো থেকে বেছে নিতে পারেন:",
                    actionType = "ACTION_OPEN_ADMIN_CHAT",
                    actionLabel = "💬 লাইভ এডমিনের সাথে কথা বলুন"
                )
            }
        }
    }
}
