<template>
	<view>
		<view  class="reader-container">
			<view :class="'content' + (loaded ? ' loaded' : ' ') " 
				:style="'width:' + (maxPageNum*100) +'%;' + ' -webkit-column-count:'+ maxPageNum+ ';' + 'column-count:'+ maxPageNum + ';' + 'left:' + leftValue +'rpx;' + 'background-color:' + (isUseBrightModel== 0?colorStyle.content_bg: 'transparent') + ';font-family:' +  currentFontFamily+ ';font-size:'+ allSliderValue.font+'rpx;'"
				@touchstart="handletouchtart" @touchmove="handletouchmove" @touchend="handletouchend">
				<view :style="'line-height:'+lineHeight +'rpx;'"> <!-- 'height:'+windows.windows_height-1 + 'rpx;'+  -->
					<!-- <text  :style="'color:'+ (colorStyle.styleNum == '4'? '#85898c': '#000800')">
				{{content}}</text> -->
				<rich-text :nodes="content"></rich-text>
				
				</view>
				<!-- <p v-html="content"></p> -->
			</view>
			<view class="cover" :style="'background-color: rgba(0, 0, 0,'+(1-allSliderValue.bright/100)+')'"></view>
			<view class="control"
				:style="'width:'+windows.windows_width+'rpx;display:'+((control.control_tab == 1 && control.all == 1)? 'flex': 'none')+';background-color:'+colorStyle.control_bg+';'">
				<view class="control-item" data-control="mulu" @tap="openMulu">
					<icon :class="'iconfont  mymulu'+colorStyle.styleNum+'-icon'">
						
					</icon>
				</view>
				<view class="control-item" data-control="jingdu" @tap="gotoControlDetail">
					<icon
						:class="(control.target == 'jingdu')? ('iconfont myjingdu'+colorStyle.styleNum+'-select-icon'): ('iconfont myjingdu'+colorStyle.styleNum+'-icon')">
					</icon>
				</view>
				<view class="control-item" data-control="color" @tap="gotoControlDetail">
					<icon
						:class="(control.target == 'color')? ('iconfont myday'+colorStyle.styleNum+'-select-icon'): ('iconfont myday'+colorStyle.styleNum+'-icon')">
					</icon>
				</view>
				<view class="control-item" data-control="font" @tap="gotoControlDetail">
					<icon :class="'iconfont myfont'+colorStyle.styleNum+'-icon'"></icon>
				</view>
			</view>
			<!-- 从这里开始-->
			<view class="control-detail"
				:style="'display:'+((control.control_detail == 1 && control.all == 1)? 'block': 'none') + ';background-color:'+colorStyle.control_bg">
				<view class="jingdu-detail" :style="'display:'+(control.target == 'jingdu'? 'block': 'none')">
					<view class="jingdu">
						<icon :class="'rleft'+colorStyle.styleNum+'-icon'"></icon>
						<view class="nameAndNum">
							<text class="faction"
								:style="'color:'+(colorStyle.styleNum == '4'? '#85898c': '#000800')">{{book.factionName}}\n</text>
							<text class="jingdu-num">第{{allSliderValue.section}}章{{' 共'+maxChapterNum}}章</text>
						</view>
						<icon :class="'rright'+colorStyle.styleNum+'-icon'"></icon>
					</view>
					<slider disabled="true" @change="sectionSliderChange" :selected-color="colorStyle.slider_bg" min="1"
						:max="maxChapterNum" :value="allSliderValue.section" />
				</view>
				<view class="color-detail" :style="'display:'+(control.target == 'color'? 'block': 'none')">
					<view class="brightness">
						<icon :class="'day'+colorStyle.styleNum+'-icon'"></icon>
						<slider class="bright-slider" @change="brightSliderChange"
							:selected-color="colorStyle.slider_bg" min="0" max="100" :value="allSliderValue.bright" />
						<icon :class="'day'+colorStyle.styleNum+'-icon'"></icon>
					</view>
					<view class="color-select">
						<view class="color-item white-bg"
							:style="'border:'+(colorStyle.styleNum == 1? '1px solid #b0d5b4': 'none')" data-stylenum="1"
							@tap="switchColorStyle"></view>
						<view class="color-item pink-bg"
							:style="'border:'+(colorStyle.styleNum == '2'? '1px solid #b0d5b4': 'none')" data-stylenum="2"
							@tap="switchColorStyle"></view>
						<view class="color-item green-bg"
							:style="'border:'+(colorStyle.styleNum == '3'? '1px solid #b0d5b4': 'none')" data-stylenum="3"
							@tap="switchColorStyle"></view>
						<view class="color-item back-bg" data-stylenum="4" @tap="switchColorStyle">
							<icon class="yueliang-icon"></icon>
						</view>
					</view>
				</view>
				<view class="font-detail" :style="'display:'+ (control.target == 'font'? 'block': 'none')">
					<view class="fontSize">
						<icon :class="'font'+colorStyle.styleNum+'-icon' + ' small'"></icon>
						<slider class="font-slider" @change="fontSliderChange" :selected-color="colorStyle.slider_bg"
							min="24" max="40" step="2" :value="allSliderValue.font" />
						<icon :class="'font'+colorStyle.styleNum+'-icon'"></icon>
					</view>
					<view class="fontSelector" @tap="selectFontFamily">
						<text>{{currentFontFamily}}</text>
					</view>
					<view class="fontDetail" :style="'background-color: '+ colorStyle.control_bg"
						:hidden="isShowFontSelector == 0">
						<view class="font-title">
							<icon :class="'leftFont'+colorStyle.styleNum+'-icon'" @tap="closeFontSelector"></icon><text
								:style="'color:'+ (colorStyle.styleNum == '4'? '#63676a': '#655f4f')">选择字体</text>
						</view>
						<view class="allItem">
							<view class="font-item" v-for="item in allFontFamily" :key="item.fontname"
								:data-fontname="item" @tap="changeFontFamily">
								<text class="fontName"
									:style="'color:'+ (currentFontFamily == item? colorStyle.control_fontColor: (colorStyle.styleNum == '4'? '#63676a': '#655f4f'))">{{item}}</text>
								<icon :type="currentFontFamily == item? 'success_no_circle': ''" size="12"
									:color="colorStyle.control_fontColor"></icon>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!--到这里结束-->
			<view :class="'mulu-detail ' + (isShowMulu == 1? 'active': '')" @tap="closeMulu">
				<view class="muluPanel" :style="'background-color: '+ colorStyle.content_bg">
					<swiper class="mulu-swipers" :current="muluSwiperNum" @change="muluSwiper">
						<swiper-item class="mulu-swiper">
							<view class="search">
								<icon type="search" size="15" color="#ccc" />
								<input type="text" name="muluStr" placeholder="搜索全书内容" placeholder-class="muluPl"
									@confirm="doContentSearch" />
							</view>
							<view class="bookInfo"
								:style="'color:'+(item.sectionNum == allSliderValue.section? colorStyle.control_fontColor: (colorStyle.styleNum == '4'? '#63676a': '#655f4f'))">
								<image :src="headImg" mode="scaleToFill"></image>
								<text class="book"><text class="factionName">{{factionName}}</text><text
										class="author">{{book.author}}</text></text>
								<text class="readerTime"><text class="num">{{hours}}</text>时<text class="num">{{minutes}}</text>分<text
										class="timeDes">读书时长</text></text>
							</view>
							<scroll-view class="sections" scroll-x="false" scroll-y="true" upper-threshold="50"
								   lower-threshold="50" scroll-top="" scroll-left="" scroll-into-view="">
								<view v-for="item in allSectionData" class="section"
									:style="'color:'+ (item.sectionNum == allSliderValue.section? colorStyle.control_fontColor: (colorStyle.styleNum == '4'? '#63676a': '#655f4f'))"
									:key="item.sectionId" :data-sectionid="item.sectionId"
									:data-sectionnum="item.sectionNum" @tap="showThisSection">
									<text class="factionNum">第{{item.sectionNum}}章</text>
									<text class="factionName">{{item.sectionTitle}}</text>
								</view>
							</scroll-view>
						</swiper-item>
						<swiper-item class="shuqian-swiper">
							目录
							
						</swiper-item>
						<swiper-item class="huaxian-swiper">
							划线
						</swiper-item>
					</swiper>
					<view class="bottomCtr">
						<text
							:style="'color:'+ (muluSwiperNum == 0? colorStyle.control_fontColor: (colorStyle.styleNum == '4'? '#63676a': '#655f4f'))">目录</text>
						<!-- {{muluSwiperNum}}和一个{{colorStyle.control_fontColor}}再和{{colorStyle.styleNum}} -->
						<text
							:style="'color:'+(muluSwiperNum == 1? colorStyle.control_fontColor: (colorStyle.styleNum == '4'? '#63676a': '#655f4f'))">书签</text>
						<text
							:style="'color:'+(muluSwiperNum == 2? colorStyle.control_fontColor: (colorStyle.styleNum == '4'? '#63676a': '#655f4f'))">划线</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 出错打印 -->
		<!-- <import src="../../component/err_tips/err_tips.wxml" />
		<template is="err_tips" data="{{err_tips_data}}" /> -->

		<!-- 成功的提示 -->
		<!-- <import src="../../component/toast/toast.wxml" />
		<template is="toast" data="{{toast_data}}" /> -->

	</view>
</template>

<script>
	import {
		GetendReading,
		GetStartReading,
		getMulu,
		getContentById,
		getContentByHistory,
		getBookDetail,
		getOneBookReadingTime,
		getmaxChapterNum,
		getChapterByNumAndBookId
	} from '../../../utils/api/api';
	import Util from '../../../utils/util';

	export default {
		data() {
			return {
				sectionNum: 1,
				maxChapterNum: 0,
				hours: 0,
				minutes:0,
				readingTime: 0,
				book: null,
				StartReadingtoken: "",				
				currentGesture: 0, //控制当一个手势进行的时候屏蔽其他的手势
				moveTime: null, //控制左滑右滑的动画
				isMoving: 0,
				leftTimmerCount: 0,
				rightTimmerCount: 0,
				hasRunTouchMove: false,
				allSectionData: null,
				loaded: false,
				bookid: '',
				factionName: '',
				headImg: '', // 小说图像
				content: '',
				factionTitle: '八部浮屠',
				windows: {
					windows_height: 0,
					pxwindows_height:0,
					windows_width: 0,
					pxwindows_width:0,
					pixelRatio: 1
				},
				touches: {
					lastX: 0,
					lastY: 0
				},
				move_direction: 0, //0代表左滑动，1代表右滑动
				leftValue: 0,
				pageIndex: 1,
				maxPageNum: 0,
				newestSectionNum: 1450,
				allSliderValue: {
					section: 1431,
					bright: 80,
					font: 40
				}, //font单位rpx
				isShowFontSelector: 0, //是否显示选择字体详情板块
				isUseBrightModel: 0,
				allFontFamily: ['微软雅黑', '黑体', 'Arial', '楷体', '等线'],
				currentFontFamily: '等线',
				lineHeight: 50, //单位rpx
				control: {
					all: 0,
					control_tab: 0,
					control_detail: 0,
					target: ''
				}, //all表示整个控制是否显示，第一点击显示，再一次点击不显示;target表示显示哪一个detail
				colorStyle: {
					content_bg: '#f5f9fc',
					styleNum: 1,
					slider_bg: '#fd9941',
					slider_none_bg: '#dbdbdb',
					control_bg: '#ffffff',
					control_fontColor: '#fd9941'
				}, //1、2、3、4分别对应四种颜色模式
				isShowMulu: 0, // 是否显示左侧栏
				muluSwiperNum: 0, //目录的滑块显示第几块
				allSectionData: [], // 所有章节数据
				err_tips_data: {
					err_tips_show: false,
					err_tips_text: ''
				}
			}
		},
		onLoad: function(options) {
			console.log("在执行onLoad打印一下options",options)
			var self = this;
			let getWindowInfo = uni.getWindowInfo()
			// console.log(getWindowInfo.screenHeight);//屏幕高度
			// console.log(getWindowInfo.screenWidth);//屏幕宽度
			self.windows.pxwindows_width = getWindowInfo.screenWidth
			self.windows.pxwindows_height = getWindowInfo.screenHeight
			//动态设置标题
			var factionName = options.name || '超品战兵';
			var bookid = options.bookid;
			self.bookid = bookid
			self.factionName = factionName
			wx.setNavigationBarTitle({
				title: factionName
			});
			uni.request({
				url:getBookDetail(self.bookid),
				success(res) {
					console.log("getBookDetail的res是",res)
					self.book = res.data.data
				}
			})
			var token= uni.getStorageSync("token")
			console.log("onload这里拿到的token",token)
			
			uni.request({
				url: getOneBookReadingTime(token,self.bookid),
				success(res) {
					console.log("getOneBookReadingTime的res是",res)
					self.hours = res.data.data.hours
					self.minutes = res.data.data.minutes
				}
			})
			uni.request({
				url:getmaxChapterNum(self.bookid),
				success:function(res){
					self.maxChapterNum = res.data.data
				}
			})
			self.loadChapter(bookid);
		},
		onShow: function() {
			console.log("在执行onshow")
			var self = this;
			//读取用户设置
			wx.getStorage({
				key: 'reader_setting',
				success: function(res) {
					var userSetting = JSON.parse(res.data);
					self.allSliderValue = userSetting.allSliderValue || self.allSliderValue
					self.allFontFamily = userSetting.allFontFamily || self.allFontFamily
					self.newestSectionNum = userSetting.newestSectionNum || self.newestSectionNum
					self.pageIndex = userSetting.pageIndex || self.pageIndex
					self.colorStyle = userSetting.colorStyle || self.colorStyle
				}
			});
			var token= uni.getStorageSync("token")
			uni.request({
				url:GetStartReading(token,self.bookid),
				fail(e) {
					console.log("GetStartReading失败",e)
				},
				success(e) {
					if(e.data.code!=200){
						console.log("GetStartReading失败了")
					}else{
						console.log("GetStartReading成功",e)
						self.StartReadingtoken = e.data.data
					}										
				}
			})
		},
		//跳出页面执行函数
		onHide: function() {
			console.log("在执行onHide")			
			var self = this;
			//onUnload方法在页面被关闭时触发，我们需要将用户的当前设置存下来
			try {
				var userSetting = {
					allSliderValue: self.allSliderValue, // 控制当前章节，亮度，字体大小
					allFontFamily: self.allFontFamily, // 已经存在的字体列表
					newestSectionNum: self.newestSectionNum, // 当前小说的最新章节
					pageIndex: self.pageIndex, // 当前第几页
					colorStyle: self.colorStyle //当前的主题
				};
				wx.setStorage('reader_setting', JSON.stringify(userSetting));
			} catch (e) {
				console.log(e);
			}
			uni.request({
				url:GetendReading(self.StartReadingtoken),
				fail(e) {
					console.log("GetendReading失败",e)
				},
				success(e) {
					console.log("GetendReading成功",e)
				}
			})
			
		},
		onUnload(){
			var self = this;			
			console.log("在执行onUnload")	
			uni.request({
				url:GetendReading(self.StartReadingtoken),
				fail(e) {
					console.log("GetendReading失败",e)
				},
				success(e) {
					console.log("GetendReading成功",e)
				}
			})
		},
		methods: {

			/**
			 * 计算总页数函数，需要理解行高---line-height和字体大小font-size之间的关系，可以查考http://www.jianshu.com/p/f1019737e155，以及http://www.w3school.com.cn/cssref/pr_dim_line-height.asp
			 * @param str 需要分页的内容
			 * @param fontSize 当前的字体大小
			 * @param lineHeight 当前的行高
			 * @param windowW 当前window的宽度
			 * @param windowH 当前window的高度
			 * @param pixelRatio 当前分辨率，用来将rpx转换成px
			 */
			countPageNum(str, fontSize, lineHeight, windowW, windowH, pixelRatio) {
				var self = this
				// console.log("在执行countPageNum")
				// console.log("fontSize是",fontSize)//32
				// console.log("lineHeight是",lineHeight)//36
				// console.log("windowW是",windowW)//320px
				// console.log("windowH是",windowH)//568px
				// var onePxToRpx = 750 / windowW;
				var myWidth = 750 ; //750rpx
				var myHeight = windowH/windowW * myWidth; //??rpx
				// console.log("pixelRatio是",pixelRatio)//1
				self.windows.windows_height = myHeight
				self.windows.windows_width = myWidth
				var returnNum = 0;
			    // fontSize = fontSize / pixelRatio;//32
				// lineHeight = lineHeight / pixelRatio;//36
				//将str根据’\n‘截成数组
				var strArray = str.split(/\n+/);//119
				// console.log("strArray长度",strArray.length)
				// console.log('strArray的值：',strArray)
				var splitArray = [];
				var reg = new RegExp('\n+', 'igm');
				var result = '';
				//这里写一个for循环去记录每处分隔符的\n的个数，这将会影响到计算换行的高度
				while ((result = reg.exec(str)) != null) {
			 	splitArray.push(result.toString().match(/\n/gim).length);
				}
				// console.log("splitArray是",splitArray)
				//spliArray比strArray少一，这里加一项使之数量一样
				splitArray.push(0);
				var totalHeight = 0;
				// console.log("一行装多少字",Math.floor((myWidth  ) / fontSize))
				
			    strArray.forEach(function(item, index) {
					//拒绝最后一项0
					var huanhangNum = (splitArray[index] -1 > 0) ? (splitArray[index] -1 )>0: 0;
					// console.log("item的length",item.length)
					totalHeight += (Math.ceil(item.length / Math.floor((myWidth-80) / fontSize)) *
						lineHeight + huanhangNum * lineHeight);
				});
				// console.log("totalHeight,",totalHeight)
				// console.log("totalHeight / myHeight",totalHeight / myHeight)
				return Math.ceil(totalHeight / myHeight)  ;
			},

			/**
			 * 发送获取目录请求的方法
			 * @param bookid 书籍的id
			 * @param sectionnum 小说的当前章节
			 * @param obj 操作的对象
			 * @param success 接口完成的回调
			 * @param fail 接口失败的回调
			 * @param preOrNext 是向前翻页还是向后翻页
			 */
			getMuluFun(bookid, sectionnum, obj, success, fail, preOrNext) {
				console.log("在执行getMuluFun")

				// console.log("看一下obj",obj)
				//obj是self
				var self = this
				//发送ajax得到这本小说的所有章节
				var oldSectionData = obj.allSectionData;
				wx.request({
					url: getMulu(bookid, sectionnum), //仅为示例，并非真实的接口地址
					success: function(res) {
						try {
							var tmpData = res.data.data;
							// console.log("看一下tmpdATA:", tmpData)
							//allSectionData像是一个队列，并不是一直加上接口获取的新数据，有一个最大值，当超过这个最大值，会在首尾删减多于的值
							if (oldSectionData.length == 0) {
								// console.log("原来数据是0")
								self.allSectionData = tmpData.sectionArray
								self.headImg = tmpData.headImg
								// console.log("现在的self.allSectionData",self.allSectionData)
							} else {
								//如果是向前翻页，数据应该插到顶部
								if (preOrNext == 'pre') {
									self.allSectionData = tmpData.sectionArray.concat(oldSectionData)
								} else if (preOrNext == 'next') {
									self.allSectionData = oldSectionData.concat(tmpData.sectionArray)
								}
							}
							//处理回调
							if (typeof success == 'function') {
								success(obj);
							}
						} catch (e) {
							console.log(e);
							if (typeof fail == 'function') {
								fail(obj);
							}
						}
					},
					fail: function(err) {
						console.log(err);
						//处理回调
						if (typeof fail == 'function') {
							fail(obj);
						}
					}
				});
			},
			loadChapter(bookid) {
				console.log("在执行loadChapter")
				//显示loading
				wx.showToast({
					title: '内容加载中',
					icon: 'loading',
					duration: 100
				});
				var self = this;
				var token = wx.getStorageSync('token');
				//根据章节id去得到章节内容
				wx.request({
					url: getContentByHistory(token, self.bookid),
					method: 'GET',
					success(res) {
						try {
							console.log("执行loadChapter，结果",res)
							var tmpData = res.data.data;													
							var newContent = tmpData.content;
							var sectionNum = tmpData.num;
							// //重新排版
							var maxPageNum = self.countPageNum(newContent, self.allSliderValue.font, self
								.lineHeight, self.windows.pxwindows_width, self.windows.pxwindows_height, self
								.windows.pixelRatio);
								// console.log("maxPageNum,",maxPageNum)
							self.content = newContent
							self.maxPageNum = maxPageNum
							self.sectionNum = sectionNum
							self.allSliderValue = {
								section: sectionNum,
								bright: self.allSliderValue.bright,
								font: self.allSliderValue.font,
							}
							wx.hideToast();
							setTimeout(() => {
								self.loaded = true
			  		}, 300);
						} catch (e) {
							wx.hideToast();
							console.log(e);
							Util.showErrMsg(self, '获取章节内容失败');
						}
					},
					fail(e) {
						wx.hideToast();
						Util.showErrMsg(self, '获取章节内容失败');
						console.error(e);
					}
				});
			},
			handletouchmove: function(event) {
				console.log('正在执行handletouchmove, event为：', event);
				var self = this;
				if (self.currentGesture != 0 || self.isMoving == 1) {
					return;
				}
				var currentX = event.touches[0].pageX;
				var currentY = event.touches[0].pageY;
				// 判断用没有滑动而是点击屏幕的动作
				self.hasRunTouchMove = true;
				console.log('正在执行touchmove, isMoving为：' + self.isMoving + '------event: {x: ' + event.touches[0].pageX + ' ,y: ' + event.touches[0].pageY + '}');
				var direction = 0;
				if (currentX - self.touches.lastX < 0) {
					direction = 0;
				} else if (currentX - self.touches.lastX > 0) {
					direction = 1;
				}
				//需要减少或者增加的值
				var moreOrLessValue = Math.abs(currentX - self.touches.lastX);
				//将当前坐标进行保存以进行下一次计算
				self.touches = {
					lastX: currentX,
					lastY: currentY
				}
				self.move_direction = direction
				var currentIndex = self.pageIndex;
				if (direction == 0) {
					if (currentIndex < self.maxPageNum) {
						self.leftValue = self.leftValue - moreOrLessValue
					}
				} else {
			  if (currentIndex > 1) {
						self.leftValue = self.leftValue + moreOrLessValue
					}
				}
			},
			handletouchtart: function(event) {
				console.log('正在执行handletouchtart, event为：', event);

				var self = this
				// 判断用户的点击事件，如果不是滑动，将不会执行touchmove
				self.hasRunTouchMove = false;
				// console.log('正在执行touchtart, isMoving为：'+isMoving+'------event: {x: '+event.touches[0].pageX+' ,y: '+event.touches[0].pageY+'}');
				if (self.isMoving == 0) {
					self.touches = {
						lastX: event.touches[0].pageX,
						lastY: event.touches[0].pageY
					}
				}
			},
			handletouchend: function() {
				var self = this;
				console.log('正在执行handletouchend, isMoving为：' + this.isMoving+", hasRunTouchMove为："+self.hasRunTouchMove);
				// 判断用户的点击事件，决定是否显示控制栏
				if (self.hasRunTouchMove == false) {
					var y = self.touches.lastY;
					var x = self.touches.lastX;
					var h = self.windows.pxwindows_height / 2;
					var w = self.windows.pxwindows_width / 2;
					// console.log("x:"+x+",y:"+y+",h:"+h+",w:"+w)
					if (x && y && y >= h - 80 && y <= h + 80 && x >= w - 80 && x <= w + 80) {
						self.control = {
							all: self.control.all == 0 ? 1 : 0,
							control_tab: 1,
							control_detail: 0,
							target: ''
						}
						self.isShowFontSelector = 0
						return;
					}
				}
				self.currentGesture = 0;
				//左滑动和有滑动的操作
				var currentIndex = self.pageIndex; //当前页数
				var targetLeftValue = null; //移动之后content的目标左值
				var pingjunValue = null; //500ms内平均每100ms移动的值
				if (self.isMoving == 0) {
					if (self.move_direction == 0) {
						if (currentIndex < self.maxPageNum) {
							targetLeftValue = -1 * self.windows.windows_width * currentIndex;
							// pingjunValue = Math.abs(targetLeftValue - self.leftValue) / 4; //500ms其实函数只执行了4次，第一次会等待100ms才会开始函数
							// isMoving = 1; //开始计时的时候将标志置1
							//使用计时器实现动画效果
							// console.log('开始向 左 滑动的计时器，isMoving为1');
							// moveTime = setInterval(function () {
							//   ++leftTimmerCount;
							//   var currentLeftValue = self.leftValue;
							//   //如果达到了目标值，立即停止计时器
							//   //调试发现有些时候这个if的跳转会莫名的不成立，所以做个限制，函数被执行了4次之后，无论条件是否成立，将leftValue设置为目标值，并结束计时器
							//   if (leftTimmerCount == 4) {
							//     clearInterval(moveTime);
			 			//     isMoving = 0;
							//     leftTimmerCount = 0;
							self.leftValue = targetLeftValue
							//     return;
			 			//   }
							//   if (currentLeftValue == targetLeftValue) {
							//     clearInterval(moveTime);
							//     isMoving = 0;
							//     leftTimmerCount = 0;
							//     // console.log('向 左 滑动的计时器结束了，isMoving为0');
							//     return;
							//   }
							//   self.setData({ leftValue: currentLeftValue - pingjunValue });
							// }, 75);
							self.pageIndex = ++currentIndex
						}else{
							//就是到了最后一页，下一步走下个目录了
							console.log("到了最后一页",self.sectionNum)
							uni.request({
								url:getChapterByNumAndBookId(self.bookid,self.sectionNum+1),
								success(res) {
									console.log("getChapterByNumAndBookId成功")
									var tmpData = res.data.data;
									console.log("看一下tmpData",tmpData)
									var newContent = tmpData.content;
									//重新排版
									var maxPageNum = self.countPageNum(newContent, self.allSliderValue.font, self
										.lineHeight, self.windows.pxwindows_width, self.windows.pxwindows_height, self
										.windows.pixelRatio);
									self.content = newContent
									self.maxPageNum = maxPageNum
									self.sectionNum +=1
									self.allSliderValue = {
										section: self.sectionNum,
										bright: self.allSliderValue.bright,
										font: self.allSliderValue.font
									}
									// self.isMoving = 0
									// self.move_direction = 0
									
									
									self.leftValue= 0
									self.pageIndex = 0
								}
								
							})
						
						}
					} else {
						//前一页和后一页相差其实是2个-320px
						if (currentIndex > 1) {
							targetLeftValue = -1 * self.windows.windows_width * (currentIndex - 2);
							// pingjunValue = Math.abs(targetLeftValue - self.leftValue) / 4;
							// isMoving = 1;
							// console.log('开始向 左 滑动的计时器，isMoving为1');
							// moveTime = setInterval(function () {
							//   ++rightTimmerCount;
							//   var currentLeftValue = self.leftValue;
							//   if (rightTimmerCount == 4) {
							//     clearInterval(moveTime);
							//     isMoving = 0;
							//     rightTimmerCount = 0;
							self.leftValue = targetLeftValue
							//     return;
							//   }
							//   if (currentLeftValue == targetLeftValue) {
							//     clearInterval(moveTime);
							//     isMoving = 0;
							//     rightTimmerCount = 0;
							//     // console.log('向 右 滑动的计时器结束了，isMoving为0');
							//     return;
							//   }
							//   self.setData({ leftValue: currentLeftValue + pingjunValue });
							// }, 75);
							self.pageIndex = --currentIndex
						}
					}
				} else {}
			},
			sectionSliderChange: function(event) {
				console.log('正在执行sectionSliderChange, event为：', event);
				var self = this;
				self.allSliderValue = {
					section: event.detail.value,
					bright: self.allSliderValue.bright,
					font: self.allSliderValue.font
				}
			},
			brightSliderChange: function(event) {
				console.log('正在执行brightSliderChange, event为：', event);

				var self = this;
				self.allSliderValue = {
					section: self.allSliderValue.section,
					bright: event.detail.value,
					font: self.allSliderValue.font
				}
				//亮度调节，其实是使用一个黑色的cover调节透明度，这里需要给出提示，使用亮度调节会失去当前颜色背景
				if (self.isUseBrightModel == 0) {
					wx.showModal({
						title: '提示',
						content: '使用亮度调节，会将背景模式还原至最初模式，建议使用系统的亮度调节',
						success: function(res) {
							if (res.confirm) {
								self.isUseBrightModel = 1
								self.colorStyle = {
									content_bg: '#f5f9fc',
									styleNum: 1,
									slider_bg: '#fd9941',
									slider_none_bg: '#dbdbdb',
									control_bg: '#ffffff',
									control_fontColor: '#fd9941'
								}
							}
						}
					});
				}
			},
			fontSliderChange: function(event) {
				console.log('正在执行fontSliderChange, event为：', event);

				var self = this;
				self.allSliderValue = {
					section: self.allSliderValue.section,
					bright: self.allSliderValue.section,
					font: event.detail.value
				}
				//重新计算分页
				var maxPageNum = self.countPageNum(self.content, event.detail.value, self.lineHeight, self.windows
					.pxwindows_width, self.windows.pxwindows_height, self.windows.pixelRatio);
					console.log("maxPageNum是",maxPageNum)
				self.maxPageNum = maxPageNum
				
			},
			gotoControlDetail: function(event) {
				console.log('正在执行gotoControlDetail, event为：', event);

				var self = this;
				var target = event.currentTarget.dataset.control;
				// 这里control_detail需要做两层判断，首先是control_detail之前是0还是1，0变成1,1变成0，其次是target在两次点击中是否相同，相同则继续上面的判断，否则取反
				var control_detail = null;
				if (self.control.control_detail == 0) {
					// 当control_detail不显示的时候不再判断两次点击的目标是否相同，直接统一显示
					control_detail = 1;
				} else {
					if (target && self.control.target == target) {
						control_detail = 0;
					} else {
						control_detail = 1;
					}
				}
				self.control = {
					all: self.control.all,
					control_tab: 1,
					control_detail: control_detail,
					target: target//font
				}
			},
			//点击切换颜色
			switchColorStyle: function(event) {
				console.log('正在执行switchColorStyle, event为：', event);

				var self = this;
				var styleNum = event.currentTarget.dataset.stylenum;
				switch (styleNum) {
					case 1:
						self.colorStyle = {
							content_bg: '#f5f9fc',
							styleNum: 1,
							slider_bg: '#fd9941',
							slider_none_bg: '#dbdbdb',
							control_bg: '#ffffff',
							control_fontColor: '#fd9941'
						}
						break;
			  case '2':
						self.colorStyle = {
							content_bg: '#f5f0da',
							styleNum: 2,
							slider_bg: '#a6832f',
							slider_none_bg: '#dbd6c3',
							control_bg: '#f8f3e0',
							control_fontColor: '#a6832f'
						}
						break;
					case '3':
						self.colorStyle = {
							content_bg: '#c0edc6',
							styleNum: 3,
							slider_bg: '#359112',
							slider_none_bg: '#a7ccab',
							control_bg: '#ccf1d0',
							control_fontColor: '#359112'
						}
						break;
					case '4':
						self.colorStyle = {
							content_bg: '#1a1e21',
							styleNum: 4,
							slider_bg: '#bb7333',
							slider_none_bg: '#212528',
							control_bg: '#101417',
							control_fontColor: '#bb7333'
						}
						break;
				}
			},
			selectFontFamily: function() {
				console.log('正在执行selectFontFamily');

				this.isShowFontSelector = 1
			},
			closeFontSelector: function() {
				console.log('正在执行closeFontSelector');

				this.isShowFontSelector = 0
			},
			changeFontFamily: function(event) {
				console.log('正在执行changeFontFamily');

				this.currentFontFamily = event.currentTarget.dataset.fontname
				//todo 执行改变字体后的重新排版
			},
			//打开目录侧边栏
			openMulu: function() {
				// console.log('正在执行openMulu');

				var self = this;
				var bookid = self.bookid || '58cbc7c8618cec336c6e8a10';
			 var sectionNum = self.allSliderValue.section || 1430;
			 // var bookid = self.bookid || '58ad9056f5d3811cecea0149'; //home
				//此接口需要分页，不能每次拿到全部的章节数据
				//发送ajax得到这本小说的所有章节
				var success = function(obj) {
					self.isShowMulu = 1
					// console.log("显示目录")
				};
				var fail = function(obj) {
					wx.showToast({
						title: '获取目录失败',
						icon: 'none'
					});
				};
				self.getMuluFun(bookid, sectionNum, self, success, fail);
			},
			//目录向上滑动到顶部
			getPreMuluPage: function() {
				console.log('正在执行getPreMuluPage');

				var self = this;
				var bookid = self.bookid || '58cbc7c8618cec336c6e8a10';
				var sectionNum = self.allSliderValue.section || 1430;
				sectionNum -= 20;
				// var bookid = self.bookid || '58ad9056f5d3811cecea0149'; //home
				var success = function(obj) {
					console.log('你向上翻了一页');
				};
				var fail = function(obj) {
					Util.showErrMsg(obj, '获取目录失败');
				};
				self.getMuluFun(bookid, sectionNum, self, success, fail, 'pre');
			},
			//目录向下滑动到底部
			getNextMuluPage: function() {
				console.log('正在执行getNextMuluPage');

				var self = this;
				var bookid = self.bookid || '58cbc7c8618cec336c6e8a10';
				var sectionNum = self.allSliderValue.section || 1430;
				sectionNum += 20;
				// var bookid = self.bookid || '58ad9056f5d3811cecea0149'; //home
				var success = function(obj) {
					console.log('你向上翻了一页');
				};
				var fail = function(obj) {
					Util.showErrMsg(obj, '获取目录失败');
				};
				self.getMuluFun(bookid, sectionNum, self, success, fail, 'next');
			},
			//滑动目录swiper
			muluSwiper: function(event) {
				console.log('正在执行muluSwiper');
				var self = this;
				var currentIndex = event.detail.current;
				self.muluSwiperNum = currentIndex
			},
			closeMulu: function(e) {
				// console.log('正在执行closeMulu');

				this.isShowMulu = 0
			},
			//点击目录某一章
			showThisSection: function(event) {
				console.log("正在执行showThisSection,event是",event)
				//显示loading
				wx.showToast({
			  title: '内容加载中',
					icon: 'loading',
					duration: 100
				});

				var self = this;
				var sectionId = event.currentTarget.dataset.sectionid;
				var sectionNum = event.currentTarget.dataset.sectionnum;
				//根据章节id去得到章节内容
				var token = uni.getStorageSync('token');
				var bookid = self.bookid ;
				
				wx.request({
					url: getContentById(sectionId,token,bookid),
					method: 'GET',
					success(res) {
						try {
							var tmpData = res.data.data;
							console.log("看一下tmpData",tmpData)
							var newContent = tmpData.content;
							//重新排版
							var maxPageNum = self.countPageNum(newContent, self.allSliderValue.font, self
								.lineHeight, self.windows.pxwindows_width, self.windows.pxwindows_height, self
								.windows.pixelRatio);
							self.content = newContent
							self.maxPageNum = maxPageNum
							self.sectionNum = tmpData.num
							self.allSliderValue = {
								section: sectionNum,
								bright: self.allSliderValue.bright,
								font: self.allSliderValue.font
							}
							// self.isMoving = 0
							// self.move_direction = 0
							
							
							self.leftValue= 0
							self.pageIndex = 0
							wx.hideToast();
							self.closeMulu();
						} catch (e) {
							// console.log(e);
							wx.hideToast();
							Util.showErrMsg(self, '获取章节内容失败');
						}
						wx.hideToast();
					},
					fail(e) {
			  	wx.hideToast();
						Util.showErrMsg(self, '获取章节内容失败');
						console.error(e);
					}
			 });
			}
		}
	}
</script>

<style>
	.reader-container {
		/* height: 100%; 看后面加回来*/
		height: 100%;
		width: 100%;
		overflow: hidden;
		/* 原来是relative ,relative比fixed好*/
		/* position: fixed; */
		position: fixed;
	}

	.reader-container .content {
		display: block;
		overflow-x: hidden;
		height: 100%;
		box-sizing: border-box;
		/* -moz-column-rule:1px solid #a3cfff; */
		/* column-rule:1px solid #a3cfff; */
		column-gap: 40rpx;
		padding: 20rpx 30rpx 0 40rpx;
		position: relative;
		left: -320rpx;
	}

	.reader-container .content.loaded {
		transition: all 0.5s ease-in-out !important;
	}

	.reader-container .control {
		width: 100%;  /* 新加的*/
		position: fixed; /* 新加的*/
		
		height: 110rpx;
		/* position: absolute; */
		bottom: 0;
		left: 0;
		display: -webkit-flex;
		display: flex;
		flex-flow: row nowrap;
		justify-content: space-around;
		border-top: 1px solid #d7d7d7;
	}

	.reader-container .control-item {
		height: 100rpx;
		margin-top: 5rpx;
		width: 100rpx;
	}

	.reader-container .control-item icon {
		height: 50rpx;
		margin-top: 25rpx;
		margin-left: 25rpx;
		width: 50rpx;
	}

	.reader-container .control-detail {
		height: 200rpx;
		width: 100%;
		position: absolute;
		bottom: 110rpx;
		left: 0;
		border-bottom: 1px solid #d7d7d7;
	}

	.reader-container .control-detail>view {
		width: 100%;
		position: absolute;
		display: none;
	}

	.reader-container .control-detail .jingdu {
		position: relative;
		text-align: center;
		font-size: 24rpx;
		color: #999;
		height: 110rpx;
	}

	.reader-container .faction {
		color: #333;
		font-size: 30rpx;
	}

	.rleft1-icon,
	.rleft2-icon,
	.rleft3-icon,
	.rleft4-icon {
		position: absolute;
		top: 25rpx;
		left: 10rpx;
		display: inline-block;
		height: 60rpx;
		width: 60rpx;
	}

	.rright1-icon,
	.rright2-icon,
	.rright3-icon,
	.rright4-icon {
		position: absolute;
		top: 25rpx;
		right: 10rpx;
		display: inline-block;
		height: 60rpx;
		width: 60rpx;
	}

	.reader-container .nameAndNum {
		text-align: center;
		padding-top: 18rpx;
	}

	.reader-container .jingdu-num {
		position: relative;
		top: 8rpx;
	}

	.reader-container .brightness {
		display: -webkit-flex;
		display: flex;
		flex-flow: row nowrap;
		justify-content: space-between;
		padding: 15rpx 20rpx 6rpx 20rpx;
		align-items: center;
	}

	.reader-container .brightness .day1-icon,
	.reader-container .brightness .day2-icon,
	.reader-container .brightness .day3-icon,
	.reader-container .brightness .day4-icon {
		width: 50rpx;
		height: 50rpx;
	}

	.reader-container .brightness .bright-slider {
		flex: 1;
	}

	.reader-container .color-select {
		display: -webkit-flex;
		display: flex;
		flex-flow: row nowrap;
		justify-content: space-around;
		padding: 16rpx 10rpx 0 10rpx;
	}

	.reader-container .color-select .color-item {
		width: 150rpx;
		height: 50rpx;
		border-radius: 8rpx;
		text-align: center;
	}

	.white-bg {
		background-color: #ffffff;
	}

	.pink-bg {
		background-color: #f8f3e0;
	}

	.green-bg {
		background-color: #ccf1d0;
	}

	.back-bg {
		background-color: #202930;
	}

	.reader-container .font-detail {
		display: -webkit-flex;
		flex-flow: column nowrap;
		justify-content: space-between;
		align-items: center;
		height: 100%;
		box-sizing: border-box;
	}

	.reader-container .fontSize {
		display: -webkit-flex;
		display: flex;
		flex-flow: row nowrap;
		justify-content: space-between;
		padding: 15rpx 20rpx 6rpx 20rpx;
		align-items: center;
		height: 50%;
	}

	.reader-container .fontSize .font1-icon,
	.reader-container .fontSize .font2-icon,
	.reader-container .fontSize .font3-icon,
	.reader-container .fontSize .font4-icon {
		width: 50rpx;
		height: 50rpx;
	}

	.reader-container .fontSize .font1-icon.small,
	.reader-container .fontSize .font2-icon.small,
	.reader-container .fontSize .font3-icon.small,
	.reader-container .fontSize .font4-icon.small {
		width: 30rpx;
		height: 30rpx;
	}

	.reader-container .fontSize .font-slider {
		flex: 1;
	}

	.reader-container .fontSelector {
		height: 40%;
		text-align: center;
		width: 100%;
		font-size: 32rpx;
	}

	.reader-container .fontSelector>text {
		height: 100%;
		width: 100%;
		line-height: 100%;
	}

	.reader-container .fontDetail {
		position: absolute;
		top: -120rpx;
		z-index: 1000;
		text-align: left;
		width: 100%;
		bottom: 0;
		padding: 0 20rpx;
		box-sizing: border-box;
		font-size: 28rpx;
	}

	.reader-container .fontDetail .allItem {
		overflow: auto;
		height: 260rpx;
	}

	.reader-container .fontDetail .font-item {
		height: 70rpx;
		border-bottom: 1px solid #d7d7d7;
		position: relative;
		line-height: 70rpx;
	}

	.reader-container .fontDetail .font-item text {
		display: inline-block;
	}

	.reader-container .fontDetail .font-item icon {
		position: absolute;
		top: 70%;
		transform: translateY(-50%);
		right: 10rpx;
	}

	.reader-container .fontDetail .font-item:last-child {
		border-bottom: none;
	}

	.reader-container .fontDetail .font-title {
		text-align: left;
		height: 60rpx;
		line-height: 60rpx;
		position: relative;
		padding-left: 70rpx;
	}

	.leftFont1-icon,
	.leftFont2-icon,
	.leftFont3-icon,
	.leftFont4-icon {
		display: inline-block;
		height: 40rpx;
		width: 46rpx;
		margin-right: 20rpx;
		position: absolute;
		top: 10rpx;
		left: 0;
	}

	.reader-container .cover {
		position: absolute;
		height: 100%;
		width: 100%;
		z-index: -1;
		top: 0;
		left: 0;
		
	}

	.yueliang-icon {
		background-size: 220rpx 220rpx;
		background-position: 4rpx -1rpx;
		display: inline-block;
		height: 38rpx;
		width: 48rpx;
		margin-top: 9rpx;
	}

     .mymulu1-icon::before{
	  content: '\e60b;';
	}
	
	.mulu1-icon {
		background-size: 240rpx 240rpx;
		background-position: -94rpx -145rpx;
	}
	.myjingdu1-icon::before{
		content: '\e610';
	}
	
	.jingdu1-icon {
		background-size: 270rpx 270rpx;
		background-position: -165rpx -166rpx;
	}
	.myjingdu1-select-icon::before{
		content: '\e975';
	}
	

	.jingdu1-select-icon {
		background-size: 270rpx 270rpx;
		background-position: -221rpx -166rpx;
	}
	
	.myday1-icon::before{
		content: '\e6d2';
	}
	.day1-icon {
		background-size: 220rpx 220rpx;
		background-position: 5rpx -176rpx;
	}
	.myday1-select-icon::before{
		content: '\e976';
	}
	.day1-select-icon {
		background-size: 260rpx 260rpx;
		background-position: -51rpx -210rpx;
	}
	.myfont1-icon::before{
		content: '\e61d';
	}
	.font1-icon {
		background-size: 270rpx 270rpx;
		background-position: -111rpx -220rpx;
	}

	.font1-icon.small {
		background-size: 180rpx 180rpx;
		background-position: -75rpx -150rpx;
	}

	.rleft1-icon {
		background-size: 270rpx 270rpx;
		background-position: -163rpx -216rpx;
	}

	.rright1-icon {
		background-size: 270rpx 270rpx;
		background-position: -215rpx -216rpx;
	}

	.leftFont1-icon {
		background-size: 220rpx 220rpx;
		background-position: 0rpx -180rpx;
	}

	.mulu2-icon {
		background-size: 220rpx 220rpx;
		background-position: -85rpx -85rpx;
	}

	.jingdu2-icon {
		background-size: 220rpx 220rpx;
		background-position: -130rpx -85rpx;
	}

	.jingdu2-select-icon {
		background-size: 220rpx 220rpx;
		background-position: -175rpx -85rpx;
	}

	.day2-icon {
		background-size: 220rpx 220rpx;
		background-position: 6rpx -130rpx;
	}

	.day2-select-icon {
		background-size: 220rpx 220rpx;
		background-position: -40rpx -130rpx;
	}

	.font2-icon {
		background-size: 270rpx 270rpx;
		background-position: -110rpx -166rpx;
	}

	.font2-icon.small {
		background-size: 180rpx 180rpx;
		background-position: -75rpx -110rpx;
	}

	.rleft2-icon {
		background-size: 270rpx 270rpx;
		background-position: -161rpx -160rpx;
	}

	.rright2-icon {
		background-size: 270rpx 270rpx;
		background-position: -213rpx -160rpx;
	}

	.leftFont2-icon {
		background-size: 220rpx 220rpx;
		background-position: 0rpx -180rpx;
	}

	.mulu3-icon {
		background-size: 220rpx 220rpx;
		background-position: -84rpx 6rpx;
	}

	.jingdu3-icon {
		background-size: 220rpx 220rpx;
		background-position: -130rpx 6rpx;
	}

	.jingdu3-select-icon {
		background-size: 220rpx 220rpx;
		background-position: -175rpx 6rpx;
	}

	.day3-icon {
		background-size: 220rpx 220rpx;
		background-position: 6rpx -40rpx;
	}

	.day3-select-icon {
		background-size: 220rpx 220rpx;
		background-position: -39rpx -40rpx;
	}

	.font3-icon {
		background-size: 270rpx 270rpx;
		background-position: -109rpx -54rpx;
	}

	.font3-icon.small {
		background-size: 180rpx 180rpx;
		background-position: -75rpx -38rpx;
	}

	.rleft3-icon {
		background-size: 270rpx 270rpx;
		background-position: -161rpx -48rpx;
	}

	.rright3-icon {
		background-size: 270rpx 270rpx;
		background-position: -213rpx -48rpx;
	}

	.leftFont3-icon {
		background-size: 220rpx 220rpx;
		background-position: 0rpx -180rpx;
	}

	.mulu4-icon {
		background-size: 240rpx 240rpx;
		background-position: -94rpx -145rpx;
	}

	.jingdu4-icon {
		background-size: 270rpx 270rpx;
		background-position: -165rpx -166rpx;
	}

	.jingdu4-select-icon {
		background-size: 270rpx 270rpx;
		background-position: -221rpx -166rpx;
	}

	.day4-icon {
		background-size: 220rpx 220rpx;
		background-position: 5rpx -176rpx;
	}

	.day4-select-icon {
		background-size: 260rpx 260rpx;
		background-position: -51rpx -210rpx;
	}

	.font4-icon {
		background-size: 270rpx 270rpx;
		background-position: -111rpx -220rpx;
	}

	.font4-icon.small {
		background-size: 180rpx 180rpx;
		background-position: -75rpx -150rpx;
	}

	.rleft4-icon {
		background-size: 270rpx 270rpx;
		background-position: -163rpx -216rpx;
	}

	.rright4-icon {
		background-size: 270rpx 270rpx;
		background-position: -215rpx -216rpx;
	}

	.leftFont4-icon {
		background-size: 220rpx 220rpx;
		background-position: 0rpx -180rpx;
	}

	.mulu-detail {
		position: absolute;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.5);
		width: 100%;
		top: 0;
		left: 0;
		display: none;
		
	}

	.mulu-detail.active {
		display: block;
	}

	.mulu-detail .muluPanel {
		width: 0%;
		height: 100%;
	}

	.mulu-detail.active .muluPanel {
		animation: slideShow 0.4s;
		animation-fill-mode: forwards;
	}

	.mulu-detail .mulu-swipers {
		height: 92%;
		/* height: 100%; */
		width: 100%;
	}

	.mulu-detail .bottomCtr {
		height: 8%;
		width: 100%;
		display: -webkit-flex;
		flex-flow: row nowarp;
		justify-content: space-between;
		align-items: center;
		padding: 0 60rpx;
		box-sizing: border-box;
		font-size: 30rpx;
	}

	.mulu-detail .mulu-swiper {
		position: relative;
	}

	.mulu-detail .mulu-swiper .search {
		border: 1px solid #ccc;
		margin: 30rpx 30rpx;
		box-sizing: border-box;
		border-radius: 8rpx;
		font-size: 28rpx;
		position: relative;
	}

	.mulu-detail .mulu-swiper .search>icon {
		position: absolute;
		left: 10rpx;
		top: 50%;
		transform: translateY(-50%);
	}

	.mulu-detail .mulu-swiper .search>input {
		margin-left: 60rpx;
	}

	.mulu-detail .mulu-swiper .bookInfo {
		margin-left: 30rpx;
		font-size: 28rpx;
	}

	.mulu-detail .mulu-swiper .bookInfo image {
		height: 80rpx;
		width: 50rpx;
	}

	.mulu-detail .mulu-swiper .bookInfo .book {
		position: absolute;
		left: 100rpx;
	}

	.mulu-detail .mulu-swiper .bookInfo .factionName {
		display: block;
	}

	.mulu-detail .mulu-swiper .bookInfo .author {
		display: block;
	}

	.mulu-detail .mulu-swiper .bookInfo .readerTime {
		position: absolute;
		right: 30rpx;
		text-align: center;
		font-size: 24rpx;
	}

	.mulu-detail .mulu-swiper .bookInfo .num {
		font-size: 28rpx;
	}

	.mulu-detail .mulu-swiper .bookInfo .timeDes {
		display: block;
		font-size: 20rpx;
	}

	.mulu-detail .mulu-swiper .sections {
		margin-top: 20rpx;
		height: 86%;
		
		/* border-top: 1rpx solid #dedede; */
		/* border-bottom: 1rpx solid #dedede; */
	}

	.mulu-detail .mulu-swiper .section {
		height: 80rpx;
		line-height: 80rpx;
		margin-bottom: 0;
		border-bottom: 1rpx solid #dedede;
		font-size: 28rpx;
		padding-left: 30rpx;
	}

	.mulu-detail .mulu-swiper .section:first-child {
		border-top: 1rpx solid #dedede;
	}

	.mulu-detail .mulu-swiper .section:last-child {
		border-bottom: 1rpx solid #dedede;
	}

	.mulu-detail .mulu-swiper .factionNum {
		margin-right: 40rpx;
	}

	@keyframes slideShow {
		form {
			width: 0;
		}

		to {
			width: 86%;
		}
	}
</style>
