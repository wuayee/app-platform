/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import * as React from 'react'

type IconProps = React.HTMLAttributes<SVGElement>

export const AppIcons = {
  LikeIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none" {...props}>
      <defs>
        <clipPath id="clip746_142060">
          <rect id="点赞" width="16.000000" height="16.000000" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <g clipPath="url(#clip746_142060)">
        <path id="合并" d="M13.7567 7.1875C13.7938 7.27148 13.8203 7.35938 13.8361 7.45068L13.8243 7.58643L13.8243 8.03223C13.8008 8.17676 13.7594 8.31494 13.7002 8.44727C13.6409 8.58008 13.5659 8.70117 13.4753 8.81201L13.4753 8.94189C13.521 9.1167 13.554 9.29395 13.5742 9.47412C13.5751 9.56494 13.5684 9.65527 13.5538 9.74512C13.5394 9.83496 13.5176 9.92285 13.4883 10.0083C13.459 10.0938 13.4226 10.1763 13.3794 10.2549C13.3362 10.3335 13.2867 10.4072 13.231 10.4766C13.2141 10.5049 13.2057 10.5356 13.2057 10.5693C13.2057 10.603 13.2141 10.6338 13.231 10.6621C13.278 10.7983 13.309 10.9385 13.3241 11.083C13.3279 11.1816 13.3234 11.2803 13.3104 11.3784C13.2975 11.4766 13.2764 11.5728 13.2471 11.667C13.2178 11.7612 13.1808 11.8516 13.136 11.9385C13.0913 12.0254 13.0396 12.1074 12.9808 12.1841C12.9543 12.2212 12.9447 12.2622 12.9518 12.3081C13.028 12.5454 13.0531 12.7891 13.0273 13.0381C13.0236 13.1143 13.0144 13.189 12.9998 13.2632C12.9851 13.3374 12.9652 13.4102 12.9399 13.4814C12.9148 13.5522 12.8846 13.6206 12.8495 13.687C12.8142 13.7529 12.7745 13.8159 12.7302 13.875C12.6859 13.9346 12.6376 13.9902 12.5852 14.042C12.5328 14.0933 12.4772 14.1401 12.418 14.1826C12.3588 14.2251 12.2969 14.2622 12.2322 14.2944C12.1676 14.3267 12.1011 14.3535 12.0325 14.375C11.8718 14.4253 11.7092 14.4604 11.5448 14.4805C11.4081 14.4971 11.27 14.5029 11.1307 14.4985L5.81335 14.4985C5.78125 14.4985 5.74915 14.4966 5.71704 14.4932C5.68506 14.4893 5.6532 14.4844 5.6217 14.4771C5.59021 14.4702 5.55896 14.4619 5.5282 14.4517C5.49744 14.4414 5.46729 14.4297 5.43762 14.4165C5.40796 14.4033 5.37891 14.3887 5.35059 14.3721C5.32227 14.356 5.2948 14.3379 5.26819 14.3188C5.24146 14.2998 5.21582 14.2793 5.19104 14.2573C5.16626 14.2354 5.14258 14.2124 5.11987 14.188C5.09729 14.1636 5.07581 14.1382 5.05554 14.1113C5.03528 14.085 5.01624 14.0571 4.99854 14.0288C4.98071 14 4.96436 13.9707 4.94934 13.9404C4.93433 13.9102 4.92078 13.8789 4.90869 13.8472C4.89648 13.8154 4.88586 13.7832 4.87671 13.7505C4.86755 13.7178 4.85986 13.6846 4.85376 13.6509C4.84766 13.6172 4.84314 13.5835 4.84021 13.5493C4.83789 13.5229 4.83655 13.4966 4.83618 13.4707C4.8335 13.8252 4.7428 14.0884 4.56421 14.2593C4.39722 14.4199 4.15344 14.5 3.83264 14.5L3.16748 14.5C2.8468 14.5 2.60291 14.4199 2.43591 14.2593C2.25464 14.0854 2.16394 13.8169 2.16394 13.4541L2.16394 8.12012C2.16394 7.75732 2.25464 7.48877 2.43591 7.31494C2.60291 7.1543 2.8468 7.07422 3.16748 7.07422L3.83264 7.07422C4.15344 7.07422 4.39722 7.1543 4.56421 7.31494C4.74084 7.48438 4.83142 7.74316 4.83606 8.0918L4.83606 7.69189C4.83911 7.60596 4.85083 7.52197 4.87122 7.43896C4.8916 7.35596 4.92004 7.27588 4.95667 7.19971C4.99341 7.12305 5.03735 7.05176 5.08862 6.98584C5.12878 6.93408 5.17261 6.88623 5.22021 6.84229C5.2334 6.83008 5.24695 6.81836 5.26074 6.80713C5.3988 6.66162 5.52686 6.50684 5.64465 6.34277C6.03442 5.72412 6.30212 5.16699 6.67444 4.48633C6.75354 4.34082 6.78845 4.18408 6.77917 4.01611C6.74426 3.50244 6.74426 2.98877 6.74426 2.47559C6.74231 2.43506 6.74329 2.39453 6.74707 2.354C6.75085 2.31396 6.75745 2.27393 6.76685 2.23486C6.77393 2.20508 6.78271 2.17578 6.79297 2.14697C6.79614 2.1377 6.79956 2.12891 6.8031 2.12012C6.81787 2.08252 6.83521 2.04639 6.8551 2.01221C6.875 1.97754 6.89709 1.94482 6.92163 1.91357C6.94397 1.88525 6.96802 1.85889 6.99365 1.83398C6.99622 1.83154 6.99866 1.8291 7.00122 1.82666C7.02979 1.7998 7.06006 1.77588 7.09204 1.75391C7.12415 1.73193 7.15747 1.7124 7.19226 1.6958C7.29871 1.63525 7.41016 1.58887 7.52686 1.55566C7.64343 1.52295 7.76208 1.50439 7.88269 1.50049C8.0033 1.49707 8.12268 1.50781 8.24084 1.53369C8.28223 1.54248 8.32312 1.55322 8.3634 1.56543C8.43835 1.58838 8.51135 1.61768 8.58264 1.65234C8.63989 1.6792 8.69385 1.71191 8.74451 1.75098C8.79517 1.79004 8.84131 1.83447 8.88293 1.88428C8.92456 1.93408 8.96082 1.98779 8.99158 2.04541C9.02234 2.10352 9.047 2.16455 9.06555 2.22803C9.22839 2.69189 9.40295 3.1499 9.5542 3.62012C9.63062 3.87793 9.67371 4.1416 9.68359 4.41113C9.68896 4.55859 9.68433 4.70459 9.66968 4.8501C9.65747 4.9707 9.63843 5.09082 9.61243 5.21045C9.53674 5.60645 9.48438 6.00879 9.42041 6.40479C9.4115 6.47754 9.4071 6.51318 9.42285 6.53027C9.43787 6.54688 9.47131 6.54688 9.53674 6.54688L12.6143 6.54688C12.813 6.54395 13.007 6.57471 13.196 6.64014C13.2778 6.67236 13.3544 6.71484 13.4257 6.76855C13.4971 6.82178 13.5608 6.88428 13.6168 6.95508C13.673 7.02637 13.7196 7.10352 13.7567 7.1875Z" clipRule="evenodd" fill="#E64545" fillOpacity="1.000000" fillRule="evenodd" />
      </g>
    </svg>
  ),
  DisLikeIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none" {...props}>
      <defs>
        <clipPath id="clip671_228369">
          <rect id="点踩" width="16.000000" height="16.000000" transform="matrix(1 0 0 -1 0 16)" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <g clipPath="url(#clip671_228369)">
        <path id="合并" d="M13.7567 8.8125C13.7938 8.72852 13.8203 8.64062 13.8361 8.54932L13.8243 8.41357L13.8243 7.96777C13.8008 7.82324 13.7594 7.68506 13.7002 7.55273C13.6409 7.41992 13.5659 7.29883 13.4753 7.18799L13.4753 7.05811C13.521 6.8833 13.554 6.70605 13.5742 6.52588C13.5751 6.43506 13.5684 6.34473 13.5538 6.25488C13.5394 6.16504 13.5176 6.07715 13.4883 5.9917C13.459 5.90625 13.4226 5.82373 13.3794 5.74512C13.3362 5.6665 13.2867 5.59277 13.231 5.52344C13.2141 5.49512 13.2057 5.46436 13.2057 5.43066C13.2057 5.39697 13.2141 5.36621 13.231 5.33789C13.278 5.20166 13.309 5.06152 13.3241 4.91699C13.3279 4.81836 13.3234 4.71973 13.3104 4.62158C13.2975 4.52344 13.2764 4.42725 13.2471 4.33301C13.2178 4.23877 13.1808 4.14844 13.136 4.06152C13.0913 3.97461 13.0396 3.89258 12.9808 3.81592C12.9543 3.77881 12.9447 3.73779 12.9518 3.69189C13.028 3.45459 13.0531 3.21094 13.0273 2.96191C13.0236 2.88574 13.0144 2.81104 12.9998 2.73682C12.9851 2.6626 12.9652 2.58984 12.9399 2.51855C12.9148 2.44775 12.8846 2.37939 12.8495 2.31299C12.8142 2.24707 12.7745 2.18408 12.7302 2.125C12.6859 2.06543 12.6376 2.00977 12.5852 1.95801C12.5328 1.90674 12.4772 1.85986 12.418 1.81738C12.3588 1.7749 12.2969 1.73779 12.2322 1.70557C12.1676 1.67334 12.1011 1.64648 12.0325 1.625C11.8718 1.57471 11.7092 1.53955 11.5448 1.51953C11.4081 1.50293 11.27 1.49707 11.1307 1.50146L5.81335 1.50146C5.78125 1.50146 5.74915 1.50342 5.71704 1.50684C5.68506 1.51074 5.6532 1.51562 5.6217 1.52295C5.59021 1.52979 5.55896 1.53809 5.5282 1.54834C5.49744 1.55859 5.46729 1.57031 5.43762 1.5835C5.40796 1.59668 5.37891 1.61133 5.35059 1.62793C5.32227 1.64404 5.2948 1.66211 5.26819 1.68115C5.24146 1.7002 5.21582 1.7207 5.19104 1.74268C5.16626 1.76465 5.14258 1.7876 5.11987 1.81201C5.09729 1.83643 5.07581 1.86182 5.05554 1.88867C5.03528 1.91504 5.01624 1.94287 4.99854 1.97119C4.98071 2 4.96436 2.0293 4.94934 2.05957C4.93433 2.08984 4.92078 2.12109 4.90869 2.15283C4.89648 2.18457 4.88586 2.2168 4.87671 2.24951C4.86755 2.28223 4.85986 2.31543 4.85376 2.34912C4.84766 2.38281 4.84314 2.4165 4.84021 2.45068C4.83789 2.47705 4.83655 2.50342 4.83618 2.5293C4.8335 2.1748 4.7428 1.91162 4.56421 1.74072C4.39722 1.58008 4.15344 1.5 3.83264 1.5L3.16748 1.5C2.8468 1.5 2.60291 1.58008 2.43591 1.74072C2.25464 1.91455 2.16394 2.18311 2.16394 2.5459L2.16394 7.87988C2.16394 8.24268 2.25464 8.51123 2.43591 8.68506C2.60291 8.8457 2.8468 8.92578 3.16748 8.92578L3.83264 8.92578C4.15344 8.92578 4.39722 8.8457 4.56421 8.68506C4.74084 8.51562 4.83142 8.25684 4.83606 7.9082L4.83606 8.30811C4.83911 8.39404 4.85083 8.47803 4.87122 8.56104C4.8916 8.64404 4.92004 8.72412 4.95667 8.80029C4.99341 8.87695 5.03735 8.94824 5.08862 9.01416C5.12878 9.06592 5.17261 9.11377 5.22021 9.15771C5.2334 9.16992 5.24695 9.18164 5.26074 9.19287C5.3988 9.33838 5.52686 9.49316 5.64465 9.65723C6.03442 10.2759 6.30212 10.833 6.67444 11.5137C6.75354 11.6592 6.78845 11.8159 6.77917 11.9839C6.74426 12.4976 6.74426 13.0112 6.74426 13.5244C6.74231 13.5649 6.74329 13.6055 6.74707 13.646C6.75085 13.686 6.75745 13.7261 6.76685 13.7651C6.77393 13.7949 6.78271 13.8242 6.79297 13.853C6.79614 13.8623 6.79956 13.8711 6.8031 13.8799C6.81787 13.9175 6.83521 13.9536 6.8551 13.9878C6.875 14.0225 6.89709 14.0552 6.92163 14.0864C6.94397 14.1147 6.96802 14.1411 6.99365 14.166C6.99622 14.1685 6.99866 14.1709 7.00122 14.1733C7.02979 14.2002 7.06006 14.2241 7.09204 14.2461C7.12415 14.2681 7.15747 14.2876 7.19226 14.3042C7.29871 14.3647 7.41016 14.4111 7.52686 14.4443C7.64343 14.4771 7.76208 14.4956 7.88269 14.4995C8.0033 14.5029 8.12268 14.4922 8.24084 14.4663C8.28223 14.4575 8.32312 14.4468 8.3634 14.4346C8.43835 14.4116 8.51135 14.3823 8.58264 14.3477C8.63989 14.3208 8.69385 14.2881 8.74451 14.249C8.79517 14.21 8.84131 14.1655 8.88293 14.1157C8.92456 14.0659 8.96082 14.0122 8.99158 13.9546C9.02234 13.8965 9.047 13.8354 9.06555 13.772C9.22839 13.3081 9.40295 12.8501 9.5542 12.3799C9.63062 12.1221 9.67371 11.8584 9.68359 11.5889C9.68896 11.4414 9.68433 11.2954 9.66968 11.1499C9.65747 11.0293 9.63843 10.9092 9.61243 10.7896C9.53674 10.3936 9.48438 9.99121 9.42041 9.59521C9.4115 9.52246 9.4071 9.48682 9.42285 9.46973C9.43787 9.45312 9.47131 9.45312 9.53674 9.45312L12.6143 9.45312C12.813 9.45605 13.007 9.42529 13.196 9.35986C13.2778 9.32764 13.3544 9.28516 13.4257 9.23145C13.4971 9.17822 13.5608 9.11572 13.6168 9.04492C13.673 8.97363 13.7196 8.89648 13.7567 8.8125Z" clipRule="evenodd" fill="#53AB6B" fillOpacity="1.000000" fillRule="evenodd" />
      </g>
    </svg>
  ),
  UnFeedbackIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none" {...props}>
      <defs>
        <clipPath id="clip806_143419">
          <rect id="未知" width="16.000000" height="16.000000" fill="white" fillOpacity="0" />
        </clipPath>
        <clipPath id="clip671_228099">
          <rect id="bg-like" width="16.000000" height="16.000000" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <g clipPath="url(#clip671_228099)">
        <g clipPath="url(#clip806_143419)">
          <path id="形状" d="M8 0C3.57 0 0 3.57 0 8C0 12.42 3.57 16 8 16C12.42 16 16 12.42 16 8C16 3.57 12.42 0 8 0Z" fill="#808080" fillOpacity="1.000000" fillRule="nonzero" />
          <path id="形状" d="M0 8C0 12.42 3.57 16 8 16C12.42 16 16 12.42 16 8C16 3.57 12.42 0 8 0C3.57 0 0 3.57 0 8Z" stroke="#000000" strokeOpacity="0" strokeWidth="1.000000" />
          <path id="形状结合" d="M7.86 10.47C8.53 10.47 9.07 11.04 9.07 11.73C9.07 12.43 8.53 13 7.86 13C7.19 13 6.65 12.43 6.65 11.73C6.65 11.04 7.19 10.47 7.86 10.47ZM7.99 3C8.82 2.99 9.63 3.3 10.2 3.85C10.75 4.37 11.03 5.05 10.99 5.75C10.94 6.55 10.6 7.12 9.61 8.01C9.16 8.41 8.91 8.69 8.9 8.82C8.85 9.29 8.43 9.64 7.92 9.64C7.37 9.6 6.89 9.14 6.95 8.63C7.02 7.99 7.34 7.51 8.25 6.69C8.87 6.13 9.02 5.94 9.03 5.65C9.04 5.47 8.96 5.29 8.8 5.13C8.6 4.94 8.3 4.83 7.99 4.83C7.43 4.83 6.96 5.23 6.96 5.73C6.96 6.23 6.52 6.64 5.98 6.64C5.43 6.64 5 6.23 5 5.73C5 4.99 5.31 4.3 5.88 3.79C6.45 3.28 7.19 3 7.99 3Z" fill="#FFFFFF" fillOpacity="1.000000" fillRule="nonzero" />
          <path id="形状结合" d="M9.07 11.73C9.07 12.43 8.53 13 7.86 13C7.19 13 6.65 12.43 6.65 11.73C6.65 11.04 7.19 10.47 7.86 10.47C8.53 10.47 9.07 11.04 9.07 11.73ZM10.2 3.85C10.75 4.37 11.03 5.05 10.99 5.75C10.94 6.55 10.6 7.12 9.61 8.01C9.16 8.41 8.91 8.69 8.9 8.82C8.85 9.29 8.43 9.64 7.92 9.64C7.37 9.6 6.89 9.14 6.95 8.63C7.02 7.99 7.34 7.51 8.25 6.69C8.87 6.13 9.02 5.94 9.03 5.65C9.04 5.47 8.96 5.29 8.8 5.13C8.6 4.94 8.3 4.83 7.99 4.83C7.43 4.83 6.96 5.23 6.96 5.73C6.96 6.23 6.52 6.64 5.98 6.64C5.43 6.64 5 6.23 5 5.73C5 4.99 5.31 4.3 5.88 3.79C6.45 3.28 7.19 3 7.99 3C8.82 2.99 9.63 3.3 10.2 3.85Z" stroke="#000000" strokeOpacity="0" strokeWidth="1.000000" />
        </g>
      </g>
    </svg>

  ),
  StarIcon: (props: IconProps) => (
    <svg width="14.000000" height="14.000000" viewBox="0 0 14 14" fill="none">
      <defs />
      <path id="path" d="M13.83 6.2C12.84 7.41 11.08 9.4 11.08 9.4C11.08 9.4 11.37 11.6 11.53 13.09C11.6 13.85 11.05 14.18 10.43 13.89C9.24 13.29 7.44 12.37 7.03 12.16C6.62 12.37 4.8 13.27 3.6 13.87C2.97 14.16 2.42 13.83 2.49 13.07C2.65 11.58 2.94 9.39 2.94 9.39C2.94 9.39 1.16 7.4 0.16 6.19C-0.19 5.75 0.03 5.13 0.73 5.02C2.16 4.75 4.37 4.32 4.37 4.32C4.37 4.32 5.58 2.09 6.35 0.72C6.77 -0.11 7.06 -0.01 7.11 0.01C7.24 0.06 7.45 0.22 7.71 0.72C8.47 2.09 9.67 4.32 9.67 4.32C9.67 4.32 11.85 4.75 13.27 5.02C13.96 5.14 14.18 5.76 13.83 6.2Z" fill="#FFC000" fillOpacity="1.000000" fillRule="nonzero" />
    </svg>
  ),
  AppLikeIcon: (props: IconProps) => (
    <svg width="18.000000" height="18.000000" viewBox="0 0 18 18" fill="none">
      <defs>
        <clipPath id="clip671_227123">
          <rect id="bg-like" width="12.000000" height="13.000000" transform="translate(3.000000 2.000000)" fill="white" fillOpacity="0" />
        </clipPath>
        <clipPath id="clip671_227122">
          <rect id="星星" width="18.000000" height="18.000000" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <g clipPath="url(#clip671_227122)">
        <g clipPath="url(#clip671_227123)">
          <path id="path" d="M15 7.95C14.98 7.85 14.95 7.77 14.92 7.68C14.88 7.6 14.83 7.52 14.78 7.45C14.72 7.38 14.66 7.32 14.58 7.26C14.51 7.21 14.44 7.17 14.36 7.13C14.17 7.07 13.97 7.04 13.77 7.04L10.7 7.04C10.56 7.04 10.56 7.04 10.58 6.9C10.64 6.5 10.7 6.1 10.77 5.71C10.83 5.44 10.85 5.18 10.84 4.91C10.83 4.64 10.79 4.37 10.71 4.12C10.56 3.64 10.39 3.19 10.22 2.72C10.21 2.66 10.18 2.6 10.15 2.54C10.12 2.48 10.08 2.43 10.04 2.38C10 2.33 9.95 2.29 9.9 2.25C9.85 2.21 9.8 2.17 9.74 2.15C9.63 2.09 9.52 2.05 9.4 2.03C9.28 2 9.16 1.99 9.04 2C8.92 2 8.8 2.02 8.69 2.05C8.57 2.08 8.46 2.13 8.35 2.19C8.32 2.21 8.28 2.23 8.25 2.25C8.22 2.27 8.19 2.29 8.16 2.32C8.13 2.35 8.11 2.38 8.08 2.41C8.06 2.44 8.03 2.47 8.01 2.51C7.99 2.54 7.98 2.58 7.96 2.62C7.95 2.65 7.94 2.69 7.93 2.73C7.92 2.77 7.91 2.81 7.91 2.85C7.9 2.89 7.9 2.93 7.9 2.97C7.9 3.48 7.9 4 7.94 4.51C7.95 4.68 7.91 4.84 7.83 4.98C7.46 5.66 7.19 6.22 6.8 6.84C6.69 7 6.56 7.16 6.42 7.3C6.36 7.35 6.3 7.41 6.25 7.48C6.2 7.55 6.15 7.62 6.12 7.69C6.08 7.77 6.05 7.85 6.03 7.93C6.01 8.02 6 8.1 6 8.19L6 13.94C5.99 13.98 6 14.01 6 14.04C6 14.08 6.01 14.11 6.01 14.15C6.02 14.18 6.03 14.21 6.04 14.25C6.04 14.28 6.06 14.31 6.07 14.34C6.08 14.37 6.09 14.41 6.11 14.44C6.12 14.47 6.14 14.5 6.16 14.52C6.18 14.55 6.19 14.58 6.21 14.61C6.23 14.63 6.26 14.66 6.28 14.68C6.3 14.71 6.33 14.73 6.35 14.75C6.37 14.77 6.4 14.79 6.43 14.81C6.45 14.83 6.48 14.85 6.51 14.87C6.54 14.88 6.57 14.9 6.6 14.91C6.63 14.92 6.66 14.94 6.69 14.95C6.72 14.96 6.75 14.97 6.78 14.97C6.81 14.98 6.84 14.98 6.88 14.99C6.91 14.99 6.94 14.99 6.97 14.99L12.29 14.99C12.6 15 12.9 14.96 13.19 14.87C13.26 14.85 13.33 14.82 13.39 14.79C13.46 14.76 13.52 14.72 13.58 14.68C13.64 14.64 13.69 14.59 13.74 14.54C13.8 14.49 13.84 14.43 13.89 14.37C13.93 14.31 13.97 14.25 14.01 14.18C14.04 14.12 14.07 14.05 14.1 13.98C14.12 13.91 14.14 13.83 14.16 13.76C14.17 13.68 14.18 13.61 14.19 13.53C14.21 13.28 14.19 13.04 14.11 12.8C14.1 12.76 14.11 12.72 14.14 12.68C14.2 12.6 14.25 12.52 14.3 12.43C14.34 12.35 14.38 12.26 14.41 12.16C14.44 12.07 14.46 11.97 14.47 11.87C14.48 11.78 14.49 11.68 14.48 11.58C14.47 11.43 14.44 11.29 14.39 11.16C14.37 11.13 14.36 11.1 14.36 11.06C14.36 11.03 14.37 11 14.39 10.97C14.45 10.9 14.5 10.83 14.54 10.75C14.58 10.67 14.62 10.59 14.65 10.5C14.68 10.42 14.7 10.33 14.71 10.24C14.73 10.15 14.73 10.06 14.73 9.97C14.71 9.79 14.68 9.61 14.63 9.44L14.63 9.31C14.73 9.2 14.8 9.08 14.86 8.94C14.92 8.81 14.96 8.67 14.98 8.53L14.98 8.08L15 7.95Z" fill="#FF6666" fillOpacity="1.000000" fillRule="nonzero" />
          <path id="path" d="M4 7.57L4.67 7.57C5.34 7.57 5.67 7.92 5.67 8.61L5.67 13.95C5.67 14.65 5.34 14.99 4.67 14.99L4 14.99C3.33 14.99 3 14.65 3 13.95L3 8.61C3 7.92 3.33 7.57 4 7.57Z" fill="#FF6666" fillOpacity="1.000000" fillRule="nonzero" />
        </g>
      </g>
    </svg>
  ),
  UserIcon: (props: IconProps) => (
    <svg width="12.000000" height="14.000000" viewBox="0 0 12 14" fill="none">
      <defs />
      <path id="path" d="M9.62 7.43C11.05 7.98 12 8.99 12 10.52L12 12.11C12 12.88 11.37 13.5 10.61 13.5L1.38 13.5C1.33 13.5 1.29 13.49 1.24 13.49C1.2 13.48 1.15 13.48 1.11 13.47C1.06 13.46 1.02 13.45 0.98 13.44C0.93 13.42 0.89 13.41 0.85 13.39C0.81 13.37 0.77 13.35 0.73 13.33C0.69 13.31 0.65 13.29 0.61 13.26C0.57 13.24 0.54 13.21 0.5 13.18C0.47 13.15 0.43 13.12 0.4 13.09C0.37 13.06 0.34 13.02 0.31 12.99C0.28 12.95 0.25 12.92 0.23 12.88C0.2 12.84 0.18 12.8 0.16 12.76C0.14 12.72 0.12 12.68 0.1 12.64C0.08 12.6 0.07 12.56 0.05 12.51C0.04 12.47 0.03 12.43 0.02 12.38C0.01 12.34 0.01 12.29 0 12.25C0 12.2 0 12.16 0 12.11L0 10.52C0 8.99 0.94 7.98 2.37 7.43C2.43 7.41 2.49 7.39 2.55 7.39C2.61 7.38 2.68 7.39 2.74 7.4C2.8 7.42 2.86 7.44 2.92 7.47C2.97 7.5 3.02 7.54 3.07 7.58C3.15 7.67 3.23 7.74 3.29 7.8C3.48 7.96 3.68 8.1 3.89 8.23C4.1 8.35 4.32 8.46 4.55 8.54C4.78 8.63 5.02 8.69 5.26 8.73C5.5 8.78 5.75 8.8 6 8.8C6.25 8.8 6.5 8.78 6.75 8.73C7 8.68 7.24 8.62 7.48 8.53C7.72 8.43 7.94 8.32 8.16 8.19C8.38 8.06 8.58 7.91 8.77 7.74C8.81 7.7 8.87 7.65 8.93 7.58C8.97 7.54 9.02 7.5 9.08 7.47C9.13 7.44 9.19 7.42 9.25 7.4C9.31 7.39 9.38 7.38 9.44 7.39C9.5 7.39 9.56 7.41 9.62 7.43ZM6 -0.75C6.11 -0.75 6.22 -0.75 6.33 -0.74C6.45 -0.73 6.56 -0.71 6.67 -0.69C6.78 -0.67 6.89 -0.64 7 -0.61C7.11 -0.57 7.21 -0.53 7.32 -0.49C7.42 -0.45 7.53 -0.4 7.63 -0.35C7.73 -0.29 7.82 -0.23 7.92 -0.17C8.01 -0.11 8.1 -0.04 8.19 0.03C8.28 0.1 8.36 0.18 8.44 0.26C8.52 0.34 8.6 0.42 8.67 0.51C8.74 0.6 8.81 0.69 8.87 0.78C8.94 0.88 8.99 0.97 9.05 1.07C9.1 1.17 9.15 1.28 9.19 1.38C9.24 1.49 9.27 1.59 9.31 1.7C9.34 1.81 9.37 1.92 9.39 2.03C9.41 2.14 9.43 2.25 9.44 2.37C9.45 2.48 9.46 2.59 9.46 2.71L9.46 4.3C9.46 4.41 9.45 4.53 9.44 4.64C9.43 4.75 9.41 4.86 9.39 4.98C9.37 5.09 9.34 5.2 9.31 5.3C9.27 5.41 9.24 5.52 9.19 5.62C9.15 5.73 9.1 5.83 9.05 5.93C8.99 6.03 8.94 6.13 8.87 6.22C8.81 6.32 8.74 6.41 8.67 6.5C8.6 6.58 8.52 6.67 8.44 6.75C8.36 6.83 8.28 6.9 8.19 6.98C8.1 7.05 8.01 7.12 7.92 7.18C7.82 7.24 7.73 7.3 7.63 7.35C7.53 7.41 7.42 7.45 7.32 7.5C7.21 7.54 7.11 7.58 7 7.61C6.89 7.64 6.78 7.67 6.67 7.69C6.56 7.72 6.45 7.73 6.33 7.74C6.22 7.76 6.11 7.76 6 7.76C5.88 7.76 5.77 7.76 5.66 7.74C5.54 7.73 5.43 7.72 5.32 7.69C5.21 7.67 5.1 7.64 4.99 7.61C4.88 7.58 4.78 7.54 4.67 7.5C4.57 7.45 4.46 7.41 4.36 7.35C4.26 7.3 4.17 7.24 4.07 7.18C3.98 7.12 3.89 7.05 3.8 6.98C3.71 6.9 3.63 6.83 3.55 6.75C3.47 6.67 3.39 6.58 3.32 6.5C3.25 6.41 3.18 6.32 3.12 6.22C3.05 6.13 3 6.03 2.94 5.93C2.89 5.83 2.84 5.73 2.8 5.62C2.75 5.52 2.72 5.41 2.68 5.3C2.65 5.2 2.62 5.09 2.6 4.98C2.58 4.86 2.56 4.75 2.55 4.64C2.54 4.53 2.53 4.41 2.53 4.3L2.53 2.71C2.53 2.59 2.54 2.48 2.55 2.37C2.56 2.25 2.58 2.14 2.6 2.03C2.62 1.92 2.65 1.81 2.68 1.7C2.72 1.59 2.75 1.49 2.8 1.38C2.84 1.28 2.89 1.17 2.94 1.07C3 0.97 3.05 0.88 3.12 0.78C3.18 0.69 3.25 0.6 3.32 0.51C3.39 0.42 3.47 0.34 3.55 0.26C3.63 0.18 3.71 0.1 3.8 0.03C3.89 -0.04 3.98 -0.11 4.07 -0.17C4.17 -0.23 4.26 -0.29 4.36 -0.35C4.46 -0.4 4.57 -0.45 4.67 -0.49C4.78 -0.53 4.88 -0.57 4.99 -0.61C5.1 -0.64 5.21 -0.67 5.32 -0.69C5.43 -0.71 5.54 -0.73 5.66 -0.74C5.77 -0.75 5.88 -0.75 6 -0.75Z" fill="#9BA9BD" fillOpacity="1.000000" fillRule="nonzero" />
    </svg>
  ),
  PreviewIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none">
      <defs>
        <clipPath id="clip7610_101783">
          <rect id="图标/16/眼睛，可见，预览-打开" width="16.000000" height="16.000000" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <g clipPath="url(#clip7610_101783)">
        <path id="path" d="M8 12C11.68 12 14.66 8 14.66 8C14.66 8 11.68 4 8 4C4.31 4 1.33 8 1.33 8C1.33 8 4.31 12 8 12Z" fill="#000000" fillOpacity="0" fillRule="nonzero" />
        <path id="path" d="M14.66 8C14.66 8 11.68 4 8 4C4.31 4 1.33 8 1.33 8C1.33 8 4.31 12 8 12C11.68 12 14.66 8 14.66 8Z" stroke="#4D4D4D" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" />
        <path id="path" d="M8 9.66C8.92 9.66 9.66 8.92 9.66 8C9.66 7.07 8.92 6.33 8 6.33C7.08 6.33 6.33 7.07 6.33 8C6.33 8.92 7.08 9.66 8 9.66Z" fill="#000000" fillOpacity="0" fillRule="nonzero" />
        <path id="path" d="M9.66 8C9.66 7.07 8.92 6.33 8 6.33C7.08 6.33 6.33 7.07 6.33 8C6.33 8.92 7.08 9.66 8 9.66C8.92 9.66 9.66 8.92 9.66 8Z" stroke="#4D4D4D" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" />
      </g>
    </svg>

  ),
  FlipIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none">
      <defs>
        <clipPath id="clip671_227175">
          <rect id="图标/16/后台悬浮运行，徽章提醒" width="16.000000" height="16.000000" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <g clipPath="url(#clip671_227175)">
        <path id="path" d="M12.18 7.35L12.16 7.33C12.16 7.05 12.38 6.83 12.66 6.83C12.94 6.83 13.16 7.05 13.16 7.33L13.14 7.35L12.18 7.35ZM8.64 2.85L8.66 2.83C8.94 2.83 9.16 3.05 9.16 3.33C9.16 3.61 8.94 3.83 8.66 3.83L8.64 3.81L8.64 2.85Z" fill="#000000" fillOpacity="0" fillRule="nonzero" />
        <path id="path" d="M12.66 7.33L12.66 13.33L2.66 13.33L2.66 3.33L8.66 3.33" stroke="#71757F" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" strokeLinecap="round" />
        <path id="path" d="M12.66 4.66C13.4 4.66 14 4.06 14 3.33C14 2.59 13.4 2 12.66 2C11.93 2 11.33 2.59 11.33 3.33C11.33 4.06 11.93 4.66 12.66 4.66Z" fill="#000000" fillOpacity="0" fillRule="nonzero" />
        <path id="path" d="M14 3.33C14 2.59 13.4 2 12.66 2C11.93 2 11.33 2.59 11.33 3.33C11.33 4.06 11.93 4.66 12.66 4.66C13.4 4.66 14 4.06 14 3.33Z" stroke="#71757F" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" />
      </g>
    </svg>
  ),
  CompleteIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none" {...props}>
      <defs />
      <rect id="ICON/16/Complete" width="16.000000" height="16.000000" fill="#FFFFFF" fillOpacity="0" />
      <path id="路径" d="M8.5 1C4.34 1 1 4.34 1 8.5C1 12.65 4.34 16 8.5 16C12.65 16 16 12.65 16 8.5C16 4.34 12.65 1 8.5 1Z" fill="#53AB6B" fillOpacity="1.000000" fillRule="evenodd" />
      <path id="路径 8" d="M4.97 8.91L4.94 8.91C4.74 8.71 4.74 8.4 4.94 8.2C5.14 8 5.45 8 5.65 8.2L5.65 8.23L5.34 8.54L11.64 6.44L11.31 6.11L11.31 6.08C11.5 5.88 11.81 5.88 12.01 6.08C12.21 6.28 12.21 6.59 12.01 6.79L11.98 6.79L11.64 6.45L7.42 10.68L5.31 8.57L4.97 8.91Z" fill="#D8D8D8" fillOpacity="0" fillRule="evenodd" />
      <path id="路径 8" d="M5.3 8.56L7.42 10.68L11.66 6.43" stroke="#FFFFFF" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" strokeLinecap="round" />
    </svg>
  ),
  PowerOffIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none" {...props}>
      <defs />
      <rect id="ICON/16/Poweroff" width="16.000000" height="16.000000" fill="#FFFFFF" fillOpacity="0" />
      <mask id="mask_1171_1324" fill="white">
        <path id="形状结合" d="M9.1084 2.06348L15.9021 13.9526C16.1689 14.4194 15.832 15 15.2944 15L1.70679 15C1.16943 15 0.83252 14.4194 1.09912 13.9526L7.89282 2.06348C8.16162 1.59326 8.8396 1.59326 9.1084 2.06348ZM8.00073 5.5C8.00073 5.22412 8.22461 5 8.50073 5C8.77686 5 9.00073 5.22412 9.00073 5.5L9.00073 10.5C9.00073 10.7764 8.77686 11 8.50073 11C8.22461 11 8.00073 10.7764 8.00073 10.5L8.00073 5.5ZM8.15063 12.1499C7.95068 12.3501 7.95068 12.6499 8.15063 12.8501C8.35059 13.0498 8.65063 13.0498 8.85059 12.8501C9.05078 12.6499 9.05078 12.3501 8.85059 12.1499C8.65063 11.9502 8.35059 11.9502 8.15063 12.1499Z" clipRule="evenodd" fill="" fillOpacity="1.000000" fillRule="evenodd" />
      </mask>
      <path id="形状结合" d="M9.1084 2.06348L15.9021 13.9526C16.1689 14.4194 15.832 15 15.2944 15L1.70679 15C1.16943 15 0.83252 14.4194 1.09912 13.9526L7.89282 2.06348C8.16162 1.59326 8.8396 1.59326 9.1084 2.06348ZM8.00073 5.5C8.00073 5.22412 8.22461 5 8.50073 5C8.77686 5 9.00073 5.22412 9.00073 5.5L9.00073 10.5C9.00073 10.7764 8.77686 11 8.50073 11C8.22461 11 8.00073 10.7764 8.00073 10.5L8.00073 5.5ZM8.15063 12.1499C7.95068 12.3501 7.95068 12.6499 8.15063 12.8501C8.35059 13.0498 8.65063 13.0498 8.85059 12.8501C9.05078 12.6499 9.05078 12.3501 8.85059 12.1499C8.65063 11.9502 8.35059 11.9502 8.15063 12.1499Z" clipRule="evenodd" fill="#F0C442" fillOpacity="1.000000" fillRule="evenodd" mask="url(#mask_1171_1324)" />
      <path id="形状结合" d="" clipRule="evenodd" fill="#979797" fillOpacity="0.000000" fillRule="evenodd" />
    </svg>
  ),
  EvalueateAllIcon: (props: IconProps) => (
    <svg width="48.000000" height="48.000000" viewBox="0 0 48 48" fill="none" {...props}>
      <defs>
        <clipPath id="clip671_224007">
          <rect id="文档类型图标" width="32.000000" height="32.000000" transform="translate(8.000000 8.000000)" fill="white" fillOpacity="0" />
        </clipPath>
      </defs>
      <rect id="画板 789" rx="4.000000" width="48.000000" height="48.000000" fill="#E6F2FF" fillOpacity="1.000000" />
      <g clipPath="url(#clip671_224007)">
        <path id="path" d="M24 9.33L36.66 16.66L36.66 31.33L23.99 38.66L11.33 31.33L11.33 16.66L24 9.33ZM16.66 21.29L22.66 24.77L22.66 31.5L25.33 31.5L25.33 24.77L31.33 21.29L30 18.98L24 22.46L18 18.98L16.66 21.29Z" fill="#1370FF" fillOpacity="1.000000" fillRule="nonzero" />
      </g>
    </svg>
  ),
  EvalueateSuccessIcon: (props: IconProps) => (
    <svg width="48.000000" height="48.000000" viewBox="0 0 48 48" fill="none" {...props}>
      <defs />
      <rect id="画板 789" rx="4.000000" width="48.000000" height="48.000000" fill="#EDFFF9" fillOpacity="1.000000" />
      <rect id="画板 785" width="32.000000" height="32.000000" transform="translate(8.000000 8.000000)" fill="#FFFFFF" fillOpacity="0" />
      <rect id="图标/16/状态/成功 Success" width="28.000000" height="28.000000" transform="translate(10.000000 10.000000)" fill="#FFFFFF" fillOpacity="0" />
      <circle id="Oval 4 Copy 5" cx="24.000000" cy="24.000000" r="14.000000" fill="#3AC295" fillOpacity="1.000000" />
      <circle id="Oval 4 Copy 5" cx="24.000000" cy="24.000000" r="13.124778" stroke="#3DCCA6" strokeOpacity="0" strokeWidth="1.750445" />
      <path id="Mask" d="M31.33 17.21L33.81 19.68L21.43 32.06L15.25 25.87L17.72 23.39L21.43 27.11L31.33 17.21Z" fill="#FFFFFF" fillOpacity="1.000000" fillRule="evenodd" />
      <path id="Mask" d="" fill="#979797" fillOpacity="0" fillRule="evenodd" />
    </svg>

  ),
  EvalueateFailIcon: (props: IconProps) => (
    <svg width="48.000000" height="48.000000" viewBox="0 0 48 48" fill="none" {...props}>
      <defs />
      <rect id="画板 789" rx="4.000000" width="48.000000" height="48.000000" fill="#FFEEED" fillOpacity="1.000000" />
      <rect id="画板 786" width="32.000000" height="32.000000" transform="translate(8.000000 8.000000)" fill="#FFFFFF" fillOpacity="0" />
      <rect id="图标/16/状态/错误 Error" width="28.000000" height="28.000000" transform="translate(10.000000 10.000000)" fill="#FFFFFF" fillOpacity="0" />
      <circle id="Oval 4 Copy 4" cx="24.000000" cy="24.000000" r="14.000000" fill="#F66F6A" fillOpacity="1.000000" />
      <circle id="Oval 4 Copy 4" cx="24.000000" cy="24.000000" r="13.124778" stroke="#F66F6A" strokeOpacity="0" strokeWidth="1.750445" />
      <path id="Mask" d="M24.12 21.64L29.07 16.69L31.54 19.17L26.59 24.12L31.54 29.07L29.07 31.54L24.12 26.59L19.17 31.54L16.7 29.07L21.64 24.12L16.7 19.17L19.17 16.69L24.12 21.64Z" fill="#FFFFFF" fillOpacity="1.000000" fillRule="evenodd" />
      <path id="Mask" d="" fill="#979797" fillOpacity="0" fillRule="evenodd" />
    </svg>
  ),
  NormalIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none">
      <defs />
      <rect id="Rectangle" x="-0.428711" rx="2.000102" width="16.000813" height="16.000000" fill="#D8D8D8" fillOpacity="0" />
      <rect id="Rectangle" x="0.271240" y="0.700195" rx="2.000102" width="14.600741" height="14.599929" stroke="#979797" strokeOpacity="0" strokeWidth="1.400071" />
      <rect id="矩形" x="0.571533" y="1.000000" rx="7.000000" width="14.000710" height="14.000000" fill="#53AB6B" fillOpacity="1.000000" />
      <rect id="矩形" x="1.271484" y="1.700195" rx="6.299964" width="12.600639" height="12.599929" stroke="#95D775" strokeOpacity="0" strokeWidth="1.400071" />
      <rect id="矩形" x="3.571777" y="4.000000" rx="4.000000" width="8.000406" height="8.000000" fill="#53AB6B" fillOpacity="0" />
      <rect id="矩形" x="4.571777" y="5.000000" rx="2.999949" width="6.000305" height="5.999898" stroke="#FFFFFF" strokeOpacity="1.000000" strokeWidth="2.000102" />
    </svg>
  ),
  AbnormalIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none">
      <defs />
      <path id="ICON" d="M7.5708 0.996094C3.68896 0.996094 0.570801 4.11426 0.570801 7.99609C0.570801 11.8779 3.68896 14.9961 7.5708 14.9961C11.4526 14.9961 14.5708 11.8779 14.5708 7.99609C14.5708 4.11426 11.4526 0.996094 7.5708 0.996094ZM7.57178 9.33398C7.97168 9.33398 8.23853 9.06738 8.23853 8.66699L8.23853 4.66699C8.23853 4.2666 7.97168 4 7.57178 4C7.17188 4 6.90503 4.2666 6.90503 4.66699L6.90503 8.66699C6.90503 9.06738 7.17188 9.33398 7.57178 9.33398ZM8.03833 10.8672C8.30518 11.1338 8.30518 11.5342 8.03833 11.8008C7.77173 12.0674 7.37183 12.0674 7.10522 11.8008C6.83838 11.5342 6.83838 11.1338 7.10522 10.8672C7.37183 10.6006 7.77173 10.6006 8.03833 10.8672Z" clip-rule="evenodd" fill="#C63939" fillOpacity="1.000000" fillRule="evenodd" />
    </svg>
  ),
  RunningIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none" >
      <defs />
      <rect id="画板 1412" width="16.000000" height="16.000000" fill="#FFFFFF" fillOpacity="0" />
      <path id="减去顶层" d="M15 8C15 4.13379 11.8662 1 8 1C4.13379 1 1 4.13379 1 8C1 11.8662 4.13379 15 8 15C11.8662 15 15 11.8662 15 8ZM4 7C3.44727 7 3 7.44775 3 8C3 8.55225 3.44727 9 4 9C4.55273 9 5 8.55225 5 8C5 7.44775 4.55273 7 4 7ZM7 8C7 7.44775 7.44727 7 8 7C8.55273 7 9 7.44775 9 8C9 8.55225 8.55273 9 8 9C7.44727 9 7 8.55225 7 8ZM12 7C11.4473 7 11 7.44775 11 8C11 8.55225 11.4473 9 12 9C12.5527 9 13 8.55225 13 8C13 7.44775 12.5527 7 12 7Z" clip-rule="evenodd" fill="#2673E5" fillOpacity="1.000000" fillRule="evenodd" />
    </svg>
  ),
  UndoIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none">
      <defs />
      <rect id="画板 1412" width="16.000000" height="16.000000" transform="translate(-0.428711 0.000000)" fill="#FFFFFF" fillOpacity="0" />
      <path id="减去顶层" d="M14.5713 8C14.5713 4.13379 11.4373 1 7.57129 1C3.70532 1 0.571289 4.13379 0.571289 8C0.571289 11.8662 3.70532 15 7.57129 15C11.4373 15 14.5713 11.8662 14.5713 8ZM3.57129 7C3.01904 7 2.57129 7.44727 2.57129 8C2.57129 8.55273 3.01904 9 3.57129 9C4.12354 9 4.57129 8.55273 4.57129 8C4.57129 7.44727 4.12354 7 3.57129 7ZM6.57129 8C6.57129 7.44727 7.01904 7 7.57129 7C8.12354 7 8.57129 7.44727 8.57129 8C8.57129 8.55273 8.12354 9 7.57129 9C7.01904 9 6.57129 8.55273 6.57129 8ZM11.5713 7C11.019 7 10.5713 7.44727 10.5713 8C10.5713 8.55273 11.019 9 11.5713 9C12.1235 9 12.5713 8.55273 12.5713 8C12.5713 7.44727 12.1235 7 11.5713 7Z" clip-rule="evenodd" fill="#808080" fillOpacity="1.000000" fillRule="evenodd" />
    </svg>
  ),
  iframeIcon: (props: IconProps) => (
    <svg width="16.000000" height="16.000000" viewBox="0 0 16 16" fill="none">
      <defs />
      <rect id="矩形备份 51" width="16.000000" height="16.000000" fill="#D8D8D8" fillOpacity="0" />
      <rect id="矩形备份 51" x="0.500000" y="0.500000" width="15.000000" height="15.000000" stroke="#979797" strokeOpacity="0" strokeWidth="1.000000" />
      <path id="路径" d="M11.12 4.51L14.49 8.07L11.12 11.5" stroke="#4D4D4D" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" strokeLinecap="round" />
      <path id="路径" d="M5.01 11.52L1.5 8.01L5.01 4.5" stroke="#4D4D4D" strokeOpacity="1.000000" strokeWidth="1.000000" strokeLinejoin="round" strokeLinecap="round" />
      <circle id="椭圆形" cx="5.060059" cy="8.009949" r="0.500000" fill="#4D4D4D" fillOpacity="1.000000" />
      <circle id="椭圆形" cx="8.060059" cy="8.009949" r="0.500000" fill="#4D4D4D" fillOpacity="1.000000" />
      <circle id="椭圆形" cx="11.060059" cy="8.009949" r="0.500000" fill="#4D4D4D" fillOpacity="1.000000" />
    </svg>
  ),
}
