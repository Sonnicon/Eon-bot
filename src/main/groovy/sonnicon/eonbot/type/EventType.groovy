package sonnicon.eonbot.type

enum EventType {
    onGenericEvent,
    onGenericUpdate,
    onRawGateway,
    onGatewayPing,

    onReady,
    onResumed,
    onReconnected,
    onDisconnect,
    onShutdown,
    onStatusChange,
    onException,

    onUserUpdateName,
    onUserUpdateDiscriminator,
    onUserUpdateAvatar,
    onUserUpdateOnlineStatus,
    onUserUpdateActivityOrder,
    onUserUpdateFlags,
    onUserTyping,
    onUserActivityStart,
    onUserActivityEnd,
    onUserUpdateActivities,

    onSelfUpdateAvatar,
    onSelfUpdateMFA,
    onSelfUpdateName,
    onSelfUpdateVerified,

    onGuildMessageReceived,
    onGuildMessageUpdate,
    onGuildMessageDelete,
    onGuildMessageEmbed,
    onGuildMessageReactionAdd,
    onGuildMessageReactionRemove,
    onGuildMessageReactionRemoveAll,
    onGuildMessageReactionRemoveEmote,

    onPrivateMessageReceived,
    onPrivateMessageUpdate,
    onPrivateMessageDelete,
    onPrivateMessageEmbed,
    onPrivateMessageReactionAdd,
    onPrivateMessageReactionRemove,

    onMessageReceived,
    onMessageUpdate,
    onMessageDelete,
    onMessageBulkDelete,
    onMessageEmbed,
    onMessageReactionAdd,
    onMessageReactionRemove,
    onMessageReactionRemoveAll,
    onMessageReactionRemoveEmote,

    onPermissionOverrideDelete,
    onPermissionOverrideUpdate,
    onPermissionOverrideCreate,

    onStoreChannelDelete,
    onStoreChannelUpdateName,
    onStoreChannelUpdatePosition,
    onStoreChannelCreate,

    onTextChannelDelete,
    onTextChannelUpdateName,
    onTextChannelUpdateTopic,
    onTextChannelUpdatePosition,
    onTextChannelUpdateNSFW,
    onTextChannelUpdateParent,
    onTextChannelUpdateSlowmode,
    onTextChannelUpdateNews,
    onTextChannelCreate,

    onVoiceChannelDelete,
    onVoiceChannelUpdateName,
    onVoiceChannelUpdatePosition,
    onVoiceChannelUpdateUserLimit,
    onVoiceChannelUpdateBitrate,
    onVoiceChannelUpdateParent,
    onVoiceChannelCreate,

    onCategoryDelete,
    onCategoryUpdateName,
    onCategoryUpdatePosition,
    onCategoryCreate,

    onPrivateChannelCreate,
    onPrivateChannelDelete,

    onGuildReady,
    onGuildTimeout,
    onGuildJoin,
    onGuildLeave,
    onGuildAvailable,
    onGuildUnavailable,
    onUnavailableGuildJoined,
    onUnavailableGuildLeave,
    onGuildBan,
    onGuildUnban,
    onGuildMemberRemove,

    onGuildUpdateAfkChannel,
    onGuildUpdateSystemChannel,
    onGuildUpdateRulesChannel,
    onGuildUpdateCommunityUpdatesChannel,
    onGuildUpdateAfkTimeout,
    onGuildUpdateExplicitContentLevel,
    onGuildUpdateIcon,
    onGuildUpdateMFALevel,
    onGuildUpdateName,
    onGuildUpdateNotificationLevel,
    onGuildUpdateOwner,
    onGuildUpdateRegion,
    onGuildUpdateSplash,
    onGuildUpdateVerificationLevel,
    onGuildUpdateLocale,
    onGuildUpdateFeatures,
    onGuildUpdateVanityCode,
    onGuildUpdateBanner,
    onGuildUpdateDescription,
    onGuildUpdateBoostTier,
    onGuildUpdateBoostCount,
    onGuildUpdateMaxMembers,
    onGuildUpdateMaxPresences,

    onGuildInviteCreate,
    onGuildInviteDelete,

    onGuildMemberJoin,
    onGuildMemberRoleAdd,
    onGuildMemberRoleRemove,

    onGuildMemberUpdate,
    onGuildMemberUpdateNickname,
    onGuildMemberUpdateBoostTime,
    onGuildMemberUpdatePending,

    onGuildVoiceUpdate,
    onGuildVoiceJoin,
    onGuildVoiceMove,
    onGuildVoiceLeave,
    onGuildVoiceMute,
    onGuildVoiceDeafen,
    onGuildVoiceGuildMute,
    onGuildVoiceGuildDeafen,
    onGuildVoiceSelfMute,
    onGuildVoiceSelfDeafen,
    onGuildVoiceSuppress,
    onGuildVoiceStream,

    onRoleCreate,
    onRoleDelete,

    onRoleUpdateColor,
    onRoleUpdateHoisted,
    onRoleUpdateMentionable,
    onRoleUpdateName,
    onRoleUpdatePermissions,
    onRoleUpdatePosition,

    onEmoteAdded,
    onEmoteRemoved,

    onEmoteUpdateName,
    onEmoteUpdateRoles,

    onHttpRequest,

    onGenericMessage,
    onGenericMessageReaction,
    onGenericGuildMessage,
    onGenericGuildMessageReaction,
    onGenericPrivateMessage,
    onGenericPrivateMessageReaction,
    onGenericUser,
    onGenericUserPresence,
    onGenericSelfUpdate,
    onGenericStoreChannel,
    onGenericStoreChannelUpdate,
    onGenericTextChannel,
    onGenericTextChannelUpdate,
    onGenericVoiceChannel,
    onGenericVoiceChannelUpdate,
    onGenericCategory,
    onGenericCategoryUpdate,
    onGenericGuild,
    onGenericGuildUpdate,
    onGenericGuildInvite,
    onGenericGuildMember,
    onGenericGuildMemberUpdate,
    onGenericGuildVoice,
    onGenericRole,
    onGenericRoleUpdate,
    onGenericEmote,
    onGenericEmoteUpdate,
    onGenericPermissionOverride,
}
