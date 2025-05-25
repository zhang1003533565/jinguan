/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",  // 扫描src目录下的所有Vue文件
  ],
  theme: {
    extend: {
      colors: {
        'dark-blue': '#1a1f3d',
        'dark-blue-light': '#232a4e',
        'dark-blue-transparent': 'rgba(26, 31, 61, 0.9)',
        'orange': '#ff7b00',
      },
    },
  },
  plugins: [],
} 