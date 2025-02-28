import i18n from "@/locale/i18n";

export const tabItems = [
  {
    label: i18n.t('all'),
    key: 'all',
  },
  {
    label: i18n.t('conversationAssistant'),
    key: 'chatbot',
  },
  {
    label: i18n.t('agent'),
    key: 'agent',
  },
  {
    label: i18n.t('workflow'),
    key: 'workflow',
  }
]
export const items = [
  {
    label: i18n.t('all'),
    key: 'all',
  },
  {
    label: i18n.t('published'),
    key: 'active',
  },
  {
    label: i18n.t('unPublished'),
    key: 'inactive',
  },
]