const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const OWNER_UID = "owner_uid_001";
const OWNER_EMAIL = "mrshosneara24@gmail.com";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function ownerDb() {
  return testEnv.authenticatedContext(OWNER_UID, {
    email: OWNER_EMAIL,
    email_verified: true,
  }).firestore();
}

function spoofedOwnerDb() {
  return testEnv.authenticatedContext("spoof_uid", {
    email: OWNER_EMAIL,
    email_verified: false,
  }).firestore();
}

function aliceDb() {
  return testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();
}

function bobDb() {
  return testEnv.authenticatedContext(BOB_UID, {
    email: "bob@example.com",
    email_verified: true,
  }).firestore();
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users are completely blocked", async () => {
  const unauth = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauth.collection("app_config").doc("main").get());
  await assertFails(unauth.collection("matches").where("status", "in", ["UPCOMING", "ROOM_OPEN"]).get());
  await assertFails(unauth.collection("bookings").where("slotNumber", ">=", 1).get());
  await assertFails(unauth.collection("player_directory").where("level", ">=", 0).get());
  await assertFails(unauth.collection("global_chat").where("timestampMillis", ">=", 0).get());
});

test("Email spoofing attack with unverified email cannot write owner config or matches", async () => {
  const past = new Date(Date.now() - 2000);
  await assertFails(
    spoofedOwnerDb().collection("app_config").doc("main").set({
      appName: "HACKED",
      officialBkashNumber: "01861583924",
      officialNagadNumber: "01861583924",
      minWithdraw: 50,
      maxWithdraw: 200,
      updatedAt: past,
    })
  );
});

test("Owner can create matches, update room credentials & slot capacity, and friends read them live", async () => {
  const past = new Date(Date.now() - 2000);
  await assertSucceeds(
    ownerDb().collection("matches").doc("m_solo_1").set({
      title: "SOLO CLASSIC SHOWDOWN",
      category: "BR_RANK",
      matchType: "SOLO",
      mapName: "বারমুডা",
      matchDateTime: "আজ রাত ৮:০০টা",
      startsInMinutes: 35,
      entryFee: 20,
      totalPrize: 350,
      perKillPrize: 10,
      booyahPrize: 100,
      totalSlots: 48,
      bookedSlots: 0,
      status: "UPCOMING",
      version: "মোবাইল অনলি",
      createdAt: past,
      updatedAt: past,
    })
  );

  // Regular player cannot create or delete matches
  await assertFails(
    aliceDb().collection("matches").doc("m_fake").set({
      title: "FAKE MATCH",
      category: "BR_RANK",
      matchType: "SOLO",
      mapName: "বারমুডা",
      matchDateTime: "আজ",
      startsInMinutes: 10,
      entryFee: 10,
      totalPrize: 100,
      perKillPrize: 5,
      booyahPrize: 50,
      totalSlots: 12,
      bookedSlots: 0,
      status: "UPCOMING",
      createdAt: past,
      updatedAt: past,
    })
  );

  // Friends can list & read live matches with matching filter
  await assertSucceeds(
    aliceDb().collection("matches").where("status", "in", ["UPCOMING", "ROOM_OPEN", "ONGOING", "FINISHED", "CANCELLED"]).get()
  );

  // Owner updates slot capacity & room credentials
  await assertSucceeds(
    ownerDb().collection("matches").doc("m_solo_1").update({
      totalSlots: 52,
      roomId: "TH9988",
      roomPassword: "123",
      status: "ROOM_OPEN",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Cross-device slot booking sync: Alice books slot, Bob sees booking & cannot overwrite match prize", async () => {
  const past = new Date(Date.now() - 3000);
  await assertSucceeds(
    ownerDb().collection("matches").doc("m1").set({
      title: "BR TOURNAMENT",
      category: "BR_RANK",
      matchType: "SOLO",
      mapName: "বারমুডা",
      matchDateTime: "রাত ৯টা",
      startsInMinutes: 20,
      entryFee: 20,
      totalPrize: 300,
      perKillPrize: 8,
      booyahPrize: 80,
      totalSlots: 20,
      bookedSlots: 0,
      status: "UPCOMING",
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertSucceeds(
    aliceDb().collection("bookings").doc("m1_slot_3").set({
      bookingId: "m1_slot_3",
      matchId: "m1",
      matchTitle: "BR TOURNAMENT",
      category: "BR_RANK",
      matchDateTime: "রাত ৯টা",
      slotNumber: 3,
      playerIgn: "ALICE_PRO",
      playerUid: "9988776655",
      trxId: "WALLET-PAY",
      feePaid: 20,
      verified: true,
      ownerUid: ALICE_UID,
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertSucceeds(
    aliceDb().collection("matches").doc("m1").update({
      bookedSlots: 1,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Shadow/poisoning test: Alice cannot modify match prize or inject ghost field
  await assertFails(
    aliceDb().collection("matches").doc("m1").update({
      totalPrize: 999999,
      bookedSlots: 2,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Bob sees synced bookings on his phone, cannot delete Alice's booking
  await assertSucceeds(bobDb().collection("bookings").where("slotNumber", ">=", 1).get());
  await assertFails(bobDb().collection("bookings").doc("m1_slot_3").delete());

  // Owner can remove any slot (Slot Remove & Refund panel)
  await assertSucceeds(ownerDb().collection("bookings").doc("m1_slot_3").delete());
});

test("PII Isolation & Role Escalation Guards on user_accounts and player_directory", async () => {
  const past = new Date(Date.now() - 2000);
  // Alice creates own public directory & private account
  await assertSucceeds(
    aliceDb().collection("player_directory").doc("u_alice").set({
      firebaseUid: ALICE_UID,
      name: "Alice",
      ign: "ALICE_FF",
      uid: "1234567890",
      role: "PLAYER",
      level: 1,
      banned: false,
      warningsCount: 0,
      moderationWarningsCount: 0,
      kudosCount: 0,
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertSucceeds(
    aliceDb().collection("user_accounts").doc("u_alice").set({
      ownerUid: ALICE_UID,
      profileId: "u_alice",
      phone: "01711111111",
      bkashNumber: "01711111111",
      role: "PLAYER",
      joinedDate: "আজ",
      createdAt: past,
      updatedAt: past,
    })
  );

  // Alice cannot self-escalate to OWNER
  await assertFails(
    aliceDb().collection("player_directory").doc("u_alice").update({
      role: "OWNER",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Alice CAN update her own safe public profile fields (IGN, tagline)
  await assertSucceeds(
    aliceDb().collection("player_directory").doc("u_alice").update({
      ign: "ALICE_LEGEND",
      battleTagline: "Ready to win",
      updatedAt: new Date(Date.now() - 800),
    })
  );

  // Bob CANNOT read Alice's private PII account
  await assertFails(bobDb().collection("user_accounts").doc("u_alice").get());
  await assertFails(bobDb().collection("user_accounts").get());
  await assertSucceeds(bobDb().collection("user_accounts").where("ownerUid", "==", BOB_UID).get());

  // Bob CAN read non-PII player directory & send kudos
  await assertSucceeds(bobDb().collection("player_directory").where("level", ">=", 0).get());
  await assertSucceeds(
    bobDb().collection("player_directory").doc("u_alice").update({
      kudosCount: 1,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Deposit, Withdraw, Badge Appeals & Moderation Logs enforce strict owner/admin boundaries", async () => {
  const past = new Date(Date.now() - 2000);
  await assertSucceeds(
    aliceDb().collection("deposit_requests").doc("dep_1").set({
      ownerUid: ALICE_UID,
      userPhone: "01711111111",
      userName: "Alice",
      userIgn: "ALICE_FF",
      amount: 100,
      paymentMethod: "BKASH",
      senderNumber: "01711111111",
      trxId: "TRX998877",
      status: "PENDING",
      createdAt: past,
      updatedAt: past,
    })
  );

  // Alice cannot self-approve her own deposit
  await assertFails(
    aliceDb().collection("deposit_requests").doc("dep_1").update({
      status: "APPROVED",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Bob cannot read Alice's deposit
  await assertFails(bobDb().collection("deposit_requests").doc("dep_1").get());

  // Owner can approve deposit
  await assertSucceeds(
    ownerDb().collection("deposit_requests").doc("dep_1").update({
      status: "APPROVED",
      adminNote: "Verified",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Moderation logs writable only by Admin/Owner
  await assertFails(
    aliceDb().collection("moderation_logs").doc("log_1").set({
      targetAuthUid: BOB_UID,
      targetUserId: "u_bob",
      targetIgn: "BOB",
      actionType: "WARN",
      reason: "Fake log",
      moderatorRole: "ADMIN",
      moderatorName: "Alice",
      timestampMillis: Date.now(),
      createdAt: past,
    })
  );

  await assertSucceeds(
    ownerDb().collection("moderation_logs").doc("log_1").set({
      targetAuthUid: ALICE_UID,
      targetUserId: "u_alice",
      targetIgn: "ALICE_FF",
      actionType: "WARN",
      reason: "Rule check",
      moderatorRole: "OWNER",
      moderatorName: "ওনার",
      timestampMillis: Date.now(),
      createdAt: past,
    })
  );
});

test("Owner and promoted Admin can publish live app announcements to app_config/main while regular players cannot", async () => {
  const past = new Date(Date.now() - 2000);
  const configPayload = {
    appName: "TRUSTED TOURNAMENT BY TH",
    appTagline: "১০০% বিশ্বস্ত • দ্রুত ক্যাশআউট",
    selectedLogo: "TH_LOGO",
    noticeTickerText: "টিকেট আপডেট",
    activeAnnouncement: "আজ রাত ৯টায় মেগা টুর্নামেন্ট লাইভ!",
    announcementTitle: "🚨 জরুরি ঘোষণা",
    announcementAuthor: "👑 ওনার (TH★DEVIL⚡)",
    announcementUpdatedAtMillis: Date.now(),
    isAnnouncementUrgent: true,
    isAnnouncementEnabled: true,
    officialBkashNumber: "01861583924",
    officialNagadNumber: "01861583924",
    officialWhatsAppUrl: "https://chat.whatsapp.com/G8TrustedTHTournament",
    minWithdraw: 50,
    maxWithdraw: 1000,
    isMaintenanceMode: false,
    maintenanceMessage: "",
    strictRules: [],
    updatedAt: past,
  };

  // Owner publishes live announcement
  await assertSucceeds(ownerDb().collection("app_config").doc("main").set(configPayload));

  // Regular player can read it live, but CANNOT modify it
  await assertSucceeds(bobDb().collection("app_config").doc("main").get());
  await assertFails(
    bobDb().collection("app_config").doc("main").set({
      ...configPayload,
      activeAnnouncement: "SPAM",
    })
  );

  // Regular player cannot grant admin rights
  await assertFails(
    aliceDb().collection("admins").doc(ALICE_UID).set({
      role: "ADMIN",
      ign: "ALICE",
      createdAt: past,
    })
  );

  // Owner promotes Alice in /admins/{uid}, then Alice can also publish live announcements
  await assertSucceeds(
    ownerDb().collection("admins").doc(ALICE_UID).set({
      role: "ADMIN",
      ign: "ALICE_ADMIN",
      createdAt: past,
    })
  );
  await assertSucceeds(
    aliceDb().collection("app_config").doc("main").set({
      ...configPayload,
      activeAnnouncement: "অ্যাডমিন থেকে লাইভ রুম কোড আপডেট!",
      announcementAuthor: "🛡️ অ্যাডমিন (ALICE_ADMIN)",
      isAnnouncementUrgent: false,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Promo codes, bonus gift drops, and live community polls enforce staff creation and anti-double-claim rules", async () => {
  const past = new Date(Date.now() - 2000);
  const nowUpdate = new Date(Date.now() - 500);

  // Regular player cannot create promo codes or bonus drops
  await assertFails(
    bobDb().collection("promo_codes").doc("promo_bob").set({
      code: "FREE100",
      title: "Fake promo",
      rewardAmount: 100,
      maxUses: 10,
      usedCount: 0,
      claimedByUids: [],
      claimedByKeys: [],
      active: true,
      createdBy: "Bob",
      createdAtMillis: Date.now(),
      createdAt: past,
      updatedAt: past,
    })
  );

  // Owner creates valid promo code, bonus drop, and poll
  await assertSucceeds(
    ownerDb().collection("promo_codes").doc("promo_th10").set({
      code: "THFF10",
      title: "ওনার স্পেশাল গিফট",
      rewardAmount: 10,
      maxUses: 5,
      usedCount: 0,
      claimedByUids: [],
      claimedByKeys: [],
      active: true,
      createdBy: "👑 ওনার",
      createdAtMillis: Date.now(),
      createdAt: past,
      updatedAt: past,
    })
  );

  // Bob can claim promo code once (+1 usedCount & Bob UID added)
  await assertSucceeds(
    bobDb().collection("promo_codes").doc("promo_th10").update({
      usedCount: 1,
      claimedByUids: [BOB_UID],
      claimedByKeys: ["01900000001"],
      updatedAt: nowUpdate,
    })
  );

  // Bob CANNOT double-claim or inflate rewardAmount
  await assertFails(
    bobDb().collection("promo_codes").doc("promo_th10").update({
      usedCount: 2,
      claimedByUids: [BOB_UID],
      claimedByKeys: ["01900000001"],
      updatedAt: new Date(Date.now() - 200),
    })
  );
  await assertFails(
    bobDb().collection("promo_codes").doc("promo_th10").update({
      rewardAmount: 500,
      updatedAt: new Date(Date.now() - 200),
    })
  );

  // Bonus Drop: Owner creates, Bob claims 1 box
  await assertSucceeds(
    ownerDb().collection("bonus_drops").doc("drop_1").set({
      title: "🎁 বোনাস রেইন",
      subtitle: "দ্রুত ক্লেইম করুন",
      amountPerClaim: 5,
      totalBoxes: 10,
      claimedCount: 0,
      claimedByUids: [],
      claimedByKeys: [],
      active: true,
      createdBy: "👑 ওনার",
      createdAtMillis: Date.now(),
      createdAt: past,
      updatedAt: past,
    })
  );
  await assertSucceeds(
    bobDb().collection("bonus_drops").doc("drop_1").update({
      claimedCount: 1,
      claimedByUids: [BOB_UID],
      claimedByKeys: ["01900000001"],
      updatedAt: nowUpdate,
    })
  );

  // Community Poll: Owner creates, Bob votes once
  await assertSucceeds(
    ownerDb().collection("community_polls").doc("poll_1").set({
      question: "পরের ম্যাচ কোন ম্যাপে চান?",
      options: [
        { id: "o1", label: "বারমুডা", votes: 0 },
        { id: "o2", label: "পুরগেটরি", votes: 0 },
      ],
      votedByUids: [],
      votedByKeys: [],
      active: true,
      createdBy: "👑 ওনার",
      createdAtMillis: Date.now(),
      createdAt: past,
      updatedAt: past,
    })
  );
  await assertSucceeds(
    bobDb().collection("community_polls").doc("poll_1").update({
      options: [
        { id: "o1", label: "বারমুডা", votes: 1 },
        { id: "o2", label: "পুরগেটরি", votes: 0 },
      ],
      votedByUids: [BOB_UID],
      votedByKeys: ["01900000001"],
      updatedAt: nowUpdate,
    })
  );
});

test("Giveaways enforce staff creation/draw, single-ticket player join, and anti-tampering", async () => {
  const past = new Date(Date.now() - 2000);
  const nowUpdate = new Date(Date.now() - 500);

  // Regular player cannot create a giveaway
  await assertFails(
    bobDb().collection("giveaways").doc("gw_fake").set({
      title: "Fake Giveaway",
      description: "Free",
      prizeType: "FF_DIAMONDS",
      prizeLabel: "100 Diamonds",
      cashAmountPerWinner: 0,
      winnersCount: 1,
      maxEntries: 50,
      entries: [],
      winners: [],
      joinedAuthUids: [],
      status: "LIVE",
      createdBy: "Bob",
      createdAtMillis: Date.now(),
      createdAt: past,
      updatedAt: past,
    })
  );

  // Owner launches live diamond giveaway
  await assertSucceeds(
    ownerDb().collection("giveaways").doc("gw_1").set({
      title: "💎 ১০০ ডায়মন্ড মেগা গিভওয়ে",
      description: "ফ্রি টিকিট নিন!",
      prizeType: "FF_DIAMONDS",
      prizeLabel: "১০০ ডায়মন্ড",
      cashAmountPerWinner: 0,
      winnersCount: 1,
      maxEntries: 20,
      entries: [],
      winners: [],
      joinedAuthUids: [],
      status: "LIVE",
      createdBy: "👑 ওনার",
      createdAtMillis: Date.now(),
      createdAt: past,
      updatedAt: past,
    })
  );

  // Bob joins giveaway once with 1 ticket
  const bobEntry = {
    actorKey: "u_bob",
    authUid: BOB_UID,
    ign: "BOB_PRO",
    uid: "99887766",
    joinedAtMillis: Date.now(),
  };
  await assertSucceeds(
    bobDb().collection("giveaways").doc("gw_1").update({
      entries: [bobEntry],
      joinedAuthUids: [BOB_UID],
      updatedAt: nowUpdate,
    })
  );

  // Bob CANNOT double-join or declare himself winner
  await assertFails(
    bobDb().collection("giveaways").doc("gw_1").update({
      entries: [bobEntry, bobEntry],
      joinedAuthUids: [BOB_UID, BOB_UID],
      updatedAt: new Date(Date.now() - 200),
    })
  );
  await assertFails(
    bobDb().collection("giveaways").doc("gw_1").update({
      winners: [{ actorKey: "u_bob", ign: "BOB_PRO", uid: "99887766", prizeLabel: "১০০ ডায়মন্ড" }],
      status: "DRAWN",
      updatedAt: new Date(Date.now() - 200),
    })
  );

  // Owner draws winner cleanly
  await assertSucceeds(
    ownerDb().collection("giveaways").doc("gw_1").update({
      winners: [{ actorKey: "u_bob", ign: "BOB_PRO", uid: "99887766", prizeLabel: "১০০ ডায়মন্ড" }],
      status: "DRAWN",
      updatedAt: new Date(Date.now() - 100),
    })
  );
});



