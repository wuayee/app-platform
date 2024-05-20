import * as React from 'react'

type IconProps = React.HTMLAttributes<SVGElement>

export const KnowledgeIcons = {
  leftArrow: (props: IconProps) => (
    <svg width="12.000000" height="21.000000" viewBox="0 0 12 21" fill="none" {...props}>
    	<defs/>
    	<path id="路径" d="M11.15 -0.28C11.67 0.24 11.67 1.08 11.15 1.6L2.66 10.09L11.15 18.57C11.63 19.06 11.66 19.82 11.25 20.34L11.15 20.46C10.62 20.98 9.78 20.98 9.26 20.46L-0.17 11.03C-0.69 10.51 -0.69 9.67 -0.17 9.15L9.26 -0.28C9.78 -0.8 10.62 -0.8 11.15 -0.28Z" fill="#4D4D4D" fill-opacity="1.000000" fill-rule="evenodd"/>
    </svg>
  ),
  add: (props: IconProps) => (
    <svg width="20.000000" height="20.000000" viewBox="0 0 20 20" fill="none" {...props}>
    	<defs>
    		<clipPath id="clip659_213374">
    			<rect id="add" width="20.000000" height="20.000000" transform="translate(-0.949219 -0.000977)" fill="white" fill-opacity="0"/>
    		</clipPath>
    	</defs>
    	<g clip-path="url(#clip659_213374)">
    		<path id="形状结合" d="M9.05 2.49C9.47 2.49 9.83 2.82 9.87 3.23L9.88 9.16L15.71 9.16C16.17 9.16 16.55 9.53 16.55 9.99C16.55 10.42 16.22 10.77 15.81 10.82L9.88 10.83L9.88 16.66C9.88 17.12 9.51 17.49 9.05 17.49C8.62 17.49 8.27 17.17 8.22 16.76L8.21 10.83L2.38 10.83C1.92 10.83 1.55 10.45 1.55 9.99C1.55 9.57 1.87 9.21 2.28 9.17L8.21 9.16L8.21 3.33C8.21 2.87 8.59 2.49 9.05 2.49Z" fill="#FFFFFF" fill-opacity="1.000000" fill-rule="evenodd"/>
    		<path id="形状结合" d="M9.87 3.23L9.88 9.16L15.71 9.16C16.17 9.16 16.55 9.53 16.55 9.99C16.55 10.42 16.22 10.77 15.81 10.82L9.88 10.83L9.88 16.66C9.88 17.12 9.51 17.49 9.05 17.49C8.62 17.49 8.27 17.17 8.22 16.76L8.21 10.83L2.38 10.83C1.92 10.83 1.55 10.45 1.55 9.99C1.55 9.57 1.87 9.21 2.28 9.17L8.21 9.16L8.21 3.33C8.21 2.87 8.59 2.49 9.05 2.49C9.47 2.49 9.83 2.82 9.87 3.23Z" stroke="#000000" stroke-opacity="0" stroke-width="1.000000"/>
    	</g>
    </svg>

  )
}