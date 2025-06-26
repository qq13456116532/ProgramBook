<template>
  <view class="page-container">
    <!-- 顶部分类导航 -->
    <view class="tabs-wrapper">
      <scroll-view class="tabs-scroll" scroll-x :show-scrollbar="false">
        <view
          class="tab-item"
          v-for="(tab, index) in classifyTypes"
          :key="index"
          :class="{ active: currentIndex === index }"
          @tap="switchTab(index)"
        >
          <text>{{ tab }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 内容区域 -->
    <scroll-view
      class="content-scroll"
      scroll-y
      enable-back-to-top
      @scrolltolower="handleLoadMore"
      @scroll="handleScroll"
      :scroll-top="scrollTopValue"
    >
      <!-- 书籍列表 -->
      <view class="book-list" v-if="classifyData.length">
        <navigator
          v-for="(book, index) in classifyData"
          :key="book.id || index"
          class="book-card"
          :url="`/pages/book_detail/book_detail?bookid=${book.id}&name=${book.factionName}`"
        >
          <image class="book-cover" :src="book.headerImage" mode="aspectFill" />
          <view class="book-info">
            <text class="book-title">{{ book.factionName }}</text>
            <text class="book-author">作者：{{ book.author }}</text>
            <text class="book-desc">{{ book.des || '暂无描述信息…' }}</text>
          </view>
        </navigator>
      </view>

      <!-- 加载状态 -->
      <view class="status-bar">
        <!-- 仅在 loading / noMore 时渲染 -->
        <uni-load-more v-if="loadStatus !== 'more'" :status="loadStatus" />
      </view>

      <!-- 空状态 -->
      <view class="empty-container" v-if="!isLoading && !classifyData.length">
        <no-data
          text="暂无书籍数据"
          btn-text="重新加载"
          show-btn
          @click="reloadData"
        />
      </view>
    </scroll-view>

    <!-- 回到顶部 -->
    <view class="back-to-top" v-show="showBackTop" @click="backToTop">
      <uni-icons type="arrow-up" size="20" color="#333" />
    </view>
  </view>
</template>

<script>
import { getClassify } from '@/utils/api/api';

export default {
  data() {
    return {
      classifyTypes: ['全部书籍', 'C_C++', 'JAVA', 'Python', '前端', '其他'],
      currentIndex: 0,

      classifyData: [],
      page: 1,
      total: 0,

      loadStatus: 'more', // 'more' | 'loading' | 'noMore'
      isLoading: false,

      requestTask: null,  // 当前在飞的 RequestTask
      reqStamp: 0,        // 自增时间戳，甄别“最新请求”

      scrollTopValue: 0,
      oldScrollTop: 0,
      showBackTop: false
    };
  },

  onLoad(options) {
    this.currentIndex = options.index ? +options.index : 0;
    this.fetchClassifyData(true);
    uni.hideShareMenu();
  },

  onUnload() {
    if (this.requestTask) {
      this.requestTask.abort();
      this.requestTask = null;
    }
  },

  methods: {
    /** ------- 网络请求封装，可同时拿到 Promise & RequestTask ------- */
    _requestWithTask(params) {
      let outTask;
      const promise = new Promise((resolve, reject) => {
        outTask = uni.request({
          ...params,
          success: res => resolve(res),
          fail: err => reject(err)
        });
      });
      return { promise, task: outTask };
    },

    /**
     * 核心数据获取
     * @param {Boolean} isRefresh 是否为刷新（切 tab / 手动刷新）
     */
    async fetchClassifyData(isRefresh = false) {
      if (!isRefresh && this.loadStatus !== 'more') return;

      /* ---- 1. 若已有请求在飞，先终止 ---- */
      if (this.requestTask) {
        this.requestTask.abort();
        this.requestTask = null;
      }

      /* ---- 2. 基础状态重置 ---- */
      if (isRefresh) {
        this.page = 1;
        this.classifyData = [];          // 立即清空，防止旧列表闪现
      }
      this.loadStatus = 'loading';
      this.isLoading = true;
      uni.showLoading({ title: '正在加载…' });

      /* ---- 3. 发起新请求并保存 task ---- */
      const stamp = ++this.reqStamp;      // 记录本次请求的时间戳
      const { promise, task } = this._requestWithTask({
        url: getClassify(this.currentIndex, this.page),
        header: { 'content-type': 'application/json' }
      });
      this.requestTask = task;           // 供下一次 abort

      try {
        const res = await promise;

        /* 若不是最后一次请求，直接丢弃结果 */
        if (stamp !== this.reqStamp) return;

        if (res.statusCode === 200 && res.data.code === 200) {
          const { list = [], total = 0 } = res.data.data || {};

          this.classifyData.push(...list);
          this.total = total;
          this.page++;

          this.loadStatus =
            this.classifyData.length >= total ? 'noMore' : 'more';
        } else {
          throw new Error(res.data?.msg || '网络异常');
        }
      } catch (err) {
        /* abort 的错误无需弹提示 */
        if (err && err.errMsg !== 'request:fail abort') {
          uni.showToast({ title: err.message || '加载失败', icon: 'none' });
        }
        if (stamp === this.reqStamp) {
          this.loadStatus = 'more';
        }
      } finally {
        /* 只有“最新请求”才能做收尾动作 */
        if (stamp === this.reqStamp) {
          this.isLoading = false;
          this.requestTask = null;
          uni.hideLoading();
        }
      }
    },

    /** 切换分类 Tab */
    switchTab(index) {
      if (this.currentIndex === index) return;

      this.currentIndex = index;
      this.scrollTopValue = this.oldScrollTop;
      this.$nextTick(() => (this.scrollTopValue = 0));

      this.fetchClassifyData(true);
    },

    /** 滚动到底部加载更多 */
    handleLoadMore() {
      this.fetchClassifyData(false);
    },

    /** 空白页重新加载 */
    reloadData() {
      this.fetchClassifyData(true);
    },

    /** 记录滚动高度并决定是否显示回到顶部按钮 */
    handleScroll(e) {
      this.oldScrollTop = e.detail.scrollTop;
      this.showBackTop = this.oldScrollTop > 200;
    },

    /** 返回顶部 */
    backToTop() {
      this.scrollTopValue = this.oldScrollTop;
      this.$nextTick(() => (this.scrollTopValue = 0));
    }
  }
};
</script>
<style lang="stylus" scoped>
// 定义颜色变量
$theme-color = #FD9941
$text-color-primary = #333333
$text-color-secondary = #666666
$text-color-light = #999999
$bg-color = #F5F5F5
$card-bg-color = #FFFFFF
$border-color = #EFEFEF

// 全局页面容器
.page-container
	display: flex
	flex-direction: column
	height: 100vh
	background-color: $bg-color

// 顶部 Tab 导航
.tabs-wrapper
	flex-shrink: 0
	background-color: $card-bg-color
	border-bottom: 1px solid $border-color
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05)
	
	.tabs-scroll
		width: 100%
		white-space: nowrap
		
		// 隐藏滚动条
		&::-webkit-scrollbar
			display: none
		
		.tab-item
			display: inline-flex
			align-items: center
			justify-content: center
			height: 90rpx
			padding: 0 30rpx
			font-size: 30rpx
			color: $text-color-secondary
			position: relative
			transition: color 0.3s ease
			
			&.active
				color: $theme-color
				font-weight: 500
				
				// 激活状态下划线
				&::after
					content: ''
					position: absolute
					bottom: 10rpx
					left: 50%
					transform: translateX(-50%)
					width: 40rpx
					height: 6rpx
					background-color: $theme-color
					border-radius: 3rpx
					transition: width 0.3s ease

// 内容滚动区域
.content-scroll
	flex: 1
	height: 0 // flex 布局下，设置为 0 才能让 flex: 1 生效
	
.book-list
	padding: 24rpx

// 书籍卡片
.book-card
	display: flex
	align-items: center
	padding: 24rpx
	background-color: $card-bg-color
	border-radius: 16rpx
	margin-bottom: 24rpx
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06)
	transition: transform 0.2s ease-in-out

	&:active
		transform: scale(0.98)

	.book-cover
		width: 140rpx
		height: 180rpx
		border-radius: 8rpx
		flex-shrink: 0
		margin-right: 24rpx
		background-color: #eee // 图片加载前的占位符颜色

	.book-info
		flex: 1
		display: flex
		flex-direction: column
		justify-content: space-around
		min-width: 0 // 防止文本过长撑开容器
		height: 180rpx
		
		.book-title
			font-size: 30rpx
			font-weight: bold
			color: $text-color-primary
			overflow: hidden
			text-overflow: ellipsis
			white-space: nowrap

		.book-author
			font-size: 26rpx
			color: $text-color-light
			margin: 8rpx 0

		.book-desc
			font-size: 26rpx
			color: $text-color-secondary
			line-height: 1.5
			overflow: hidden
			text-overflow: ellipsis
			// 多行文本截断
			display: -webkit-box
			-webkit-line-clamp: 2
			-webkit-box-orient: vertical

// 状态提示条
.status-bar
	padding: 20rpx 0
	
// 空状态容器
.empty-container
	display: flex
	justify-content: center
	align-items: center
	padding-top: 200rpx

// 返回顶部按钮
.back-to-top
	position: fixed
	right: 30rpx
	bottom: 120rpx
	width: 88rpx
	height: 88rpx
	border-radius: 50%
	background-color: $card-bg-color
	display: flex
	justify-content: center
	align-items: center
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1)
	z-index: 99
</style>