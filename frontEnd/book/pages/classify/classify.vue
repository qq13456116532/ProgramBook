<template>
	<view class="classify-container">
	  
	  <view class="parent">
		  <view  class="tab" style="position: absolute; top: 0; left: 0; right: 0;">
		    <view @tap="showClassify"  :class="'tab-item '+ (currentIndex == tabIndex? 'active': '')" v-for="(tabItem,tabIndex) in classifyTypes" :key="tabIndex" :data-index="tabIndex" >
		      <text>{{tabItem}}</text>
		      <view v-if="currentIndex == tabIndex" class="tabLine"></view>
		    </view>
		  </view>
		  <scroll-view
		   class="tab-content" enable-back-to-top="true" scroll-y="true"
			@scrolltolower="loadMoreData" :style="'position: relative;padding-top: '+(classifyData.length == 0 ? '60rpx' : '30rpx')"></scroll-view>
	   </view>
		<!-- 暂无数据 -->
		<scroll-view
		 class="tab-content" enable-back-to-top="true" scroll-y="true"
					@scrolltolower="loadMoreData" :style="'position: relative;padding-top: '+(classifyData.length == 0 ? '60rpx' : '30rpx')">		
	    <no-data v-if="classifyData.length == 0 ? true : false" text="暂无数据" btn-text="重新获取" show-btn="true" @click = "reloadData" bindbtnclick="reloadData"></no-data>
		<navigator :url="'../book_detail/book_detail?bookid=' + bookItem.id + '&name=' + bookItem.factionName " v-if="classifyData.length > 0"  v-for="(bookItem,bookIndex) in classifyData"  :key="id">
	      <view class="book-item" :data-bookid="bookItem.id">
	        <view class="bookInfo">
	          <text class="bookName"><text class="des">书名:</text>{{bookItem.factionName}}\n</text>
	          <text class="author"><text class="des">作者:</text>{{bookItem.author}}\n</text>
	          <text class="bookDes"><text class="des">描述:</text>{{bookItem.des.length<30?"本书是一种介绍编程语言、技术或概念的书籍，通常包含代码示例、练习和项目，帮助读者学习和实践编程。编程书有不同的难度级别，适合不同的读者群体，从初学者到专家。编程书可以涵盖各种主题，如网页开发、数据科学、人工智能、游戏设计等。":bookItem.des}}</text>
	          <icon :hidden="bookIndex == 0" class="first-icon"></icon>
	        </view>
	        <image :src="bookItem.headerImage" mode="scaleToFill" />
	      </view>
	    </navigator>
	  </scroll-view>
	  <view class="backTop" @click="backToTop" :hidden="!scrollTop.backTop_show">
	    <icon class="backTop-icon"></icon>
	  </view>
	</view>

</template>

<script>
	import { getClassify } from '../../utils/api/api'

	export default {
		data() {
			return {
				 classifyTypes: ['全部书籍', 'C_C++', 'JAVA', 'Python', '前端', '其他'],
				    page: 1,
				    total: 0,
				    classifyData: [],
				    currentIndex: 0,
				    scrollTop: { scrollTop_value: 0, backTop_show: false },
				    hasSrollBottom: false
			};
		},
		 onLoad: function (options) {
		    var self = this;
		    //根据url中传过来的分类index，加载指定的分类数据，index默认值1
		    if (options.index) {
			  this.currentIndex = options.index
		      self.getClassifyData(options.index, self.page);
		    } else {
		      self.getClassifyData(self.currentIndex, self.page);
		    }
		    // 当前页面不予许分享
		    uni.hideShareMenu();
		  },
		  methods:{
			   getClassifyData: function (index, page, isLoadMore) {
			      //显示加载中
			      uni.showToast({ title: '加载中', icon: 'loading' });
			      let self = this;
			      if (!isLoadMore) {
			        page = 1;
					this.page=1
			      }
			      uni.request({
			        url: getClassify(index, page),
			        header: { 'content-type': 'application/json' },
			        success: function (res) {
			        console.log('Debug: res', res);
			          const data = res.data.data;
			          //隐藏加载信息  
			          setTimeout(function () {
			            uni.hideToast();
			          }, 300);
			          if (data.list) {
			            if (isLoadMore) {
			              self.classifyData = self.classifyData.concat(data.list)
						  self.total = data.total
			            } else {
							self.classifyData = data.list
							self.total = data.total
			            }
			          } else {
			            uni.showToast({ title: '获取书籍分类失败' + data.msg ? '，' + data.msg : '', icon: 'none' });
			          }
			        },
			        error: function (err) {
			          setTimeout(function () {
			            uni.hideToast();
			            uni.showToast({ title: '获取分类数据失败~', icon: 'none' });
			          }, 500);
			        }
			      });
			    },
			    showClassify: function (event) {
					console.log("正在执行showClassify，event是",event)
					this.currentIndex = event.target.index 
			      this.getClassifyData(this.currentIndex, this.page);
			    },
			    loadMoreData: function (event) {
			      let page = this.page + 1;
			      if (page >= Math.ceil(this.total / 8) + 1) {
			        if (!this.hasScrollBottom) {
			          uni.showToast({ title: '暂无更多数据~' });
					  this.hasScrollBottom = true;
			        }
			      } else {
					  this.page = page
			        this.getClassifyData(this.currentIndex, this.page, true);
			      }
			    },
			    backToTop: function () {
			      var topValue = this.scrollTop.scrollTop_value; //发现设置scroll-top值不能和上一次的值一样，否则无效，所以这里加了个判断
			      if (topValue == 1) {
			        topValue = 0;
			      } else {
			        topValue = 1;
			      }
				  this.scrollTop.scrollTop_value = topValue
			    },
			    reloadData: function () {
			      this.getClassifyData(this.currentIndex, this.page);
			    }
		  }
	}
</script>

<style lang="stylus">
page {
  height: 100%;
}

.classify-container {
  position: relative;
  overflow: hidden;
  height: 100%;
}

.classify-container .tab {
	//这个属性表示是否被覆盖,'溢出-xoverflow-x设置为auto，如果内容溢出 x 轴上的元素框，则该元素将显示水平滚动条。这允许用户水平滚动以查看当前不可见的内容。
  overflow-x: auto;
  white-space: nowrap;
  height: 7.4%;
  top: 0;
  left: 0;
  padding: 0 15rpx;
  font-weight: bold;
  border-bottom: 1px solid #dddddd;
}

/*可滑动但是不显示滚动条*/

.classify-container .tab::-webkit-scrollbar {
  width: 0;
  height: 0;
  color: transparent;
}

.classify-container .tab .tab-item {
  line-height: 80rpx;
  height: 80rpx;
 display: inline-block;
  margin-right: 45rpx;
  width: auto;
  text-align: center;
  font-size: 30rpx;
  color: #888888;
}

.classify-container .tab .tab-item.active {
  color: #FD9941;
}

.classify-container .tab .tab-item .tabLine {
  height: 6rpx;
  width: 30rpx;
  border-radius: 3rpx;
  margin: 0 auto;
  background-color: #FD9941;
  z-index: 1000;
  margin-top: -15rpx;
}

.classify-container .tab-content {
  height: 93.6%;
  padding: 30rpx 0;
}

.classify-container .book-item {
  /*高度塌陷*/
  overflow: hidden;
  padding: 0 15rpx;
  position: relative;
  margin-bottom: 20rpx;
}

.classify-container .book-item > image {
  height: 180rpx;
  width: 140rpx;
  box-shadow: 4rpx 4rpx 2rpx #888;
  display: inline-block;
  position: absolute;
  top: 50%;
  margin-top: -84rpx;
  left: 10px;
}

.classify-container .book-item .bookInfo {
  width: 100%;
  padding: 10rpx 10rpx 10rpx 180rpx;
  font-size: 22rpx;
  box-sizing: border-box;
  position: relative;
}

.classify-container .book-item .bookInfo::after {
  content: '';
  display: none;
  clear: both;
}

.classify-container .book-item > text {
  display: block;
  color: #4d5154;
}

.classify-container .bookInfo .bookName {
  font-size: 28rpx;
  font-weight: bold;
  color: #4c4d4f;
}

.classify-container .bookInfo .des {
  font-size: 24rpx;
  color: #888888;
  margin-right: 10rpx;
  vertical-align: middle;
}

.classify-container .bookInfo .author {
  font-size: 24rpx;
  margin-top: 10rpx;
  color: #737a80;
  line-height: 46rpx;
}

.classify-container .bookInfo .bookDes {
  font-size: 24rpx;
  color: #5a5f62;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.classify-container .bookInfo .text-underline {
  border-bottom: 4rpx solid #FD9941;
  margin-right: 10rpx;
  color: #888;
}

.classify-container .bookInfo > icon {
  position: absolute;
  right: 20rpx;
  top: 10rpx;
  display: inline-block;
  width: 30rpx;
  height: 45rpx;
}

.classify-container .backTop {
  position: fixed;
  bottom: 70rpx;
  right: 50rpx;
  height: 80rpx;
  width: 80rpx;
  background-color: #d7d7d7;
  z-index: 1000;
  text-align: center;
}

.classify-container .backTop .backTop-icon {
  background-size: 300rpx 300rpx;
  background-position: -244rpx -245rpx;
  display: inline-block;
  width: 60rpx;
  height: 60rpx;
  margin-top: 10rpx;
}

</style>
