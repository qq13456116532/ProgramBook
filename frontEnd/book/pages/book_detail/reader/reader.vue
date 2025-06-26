<template>
	<view>
		<view class="reader-container">
			<!-- Main Content Area -->
			<view :class="['content', { loaded: loaded }]" :style="contentStyle" @touchstart="handleTouchStart"
				@touchmove="handleTouchMove" @touchend="handleTouchEnd">
				<view :style="contentInnerStyle">
					<rich-text :nodes="content"></rich-text>
				</view>
			</view>

			<!-- Brightness Overlay -->
			<view class="cover" :style="coverStyle"></view>

			<!-- Bottom Control Bar -->
				<!-- 添加 @touchend.stop 来阻止事件穿透 -->
			<view v-show="control_all && control_tab" class="control" :style="controlPanelStyle">
				<view class="control-item" @tap="openMulu">
					<icon :class="muluIconClass" />
				</view>
				<!-- 注意 @tap 后面增加了 .stop -->
				
				<view class="control-item" @tap="showControlDetail('jingdu')">
				    <icon :class="jingduIconClass" />
				</view>
				<view class="control-item" @tap="showControlDetail('color')">
				    <icon :class="dayIconClass" />
				</view>
				<view class="control-item" @tap="showControlDetail('font')">
				    <icon :class="fontIconClass" />
				</view>
			</view>

			<!-- Detailed Control Panels -->
			<!-- 同样添加 @touchend.stop -->
			<view v-show="control_all && control_detail" class="control-detail" :style="controlPanelStyle">
				<!-- Progress Detail -->
				<view v-show="control_target === 'jingdu'" class="jingdu-detail">
					<view class="jingdu">
						<icon :class="rLeftIconClass" @tap="prevChapter"></icon>
						<view class="nameAndNum">
							<text class="faction" :style="secondaryTextStyle">{{ book.factionName }}</text><br/>
							<text class="jingdu-num">第{{ sliderValues.section }}章 / 共{{ maxChapterNum }}章</text>
						</view>
						<icon :class="rRightIconClass" @tap="nextChapter"></icon>
					</view>
					<slider disabled :value="sliderValues.section" min="1" :max="maxChapterNum"
						:active-color="colorStyle.slider_bg" @change="sectionSliderChange" />
				</view>

				<!-- Color & Brightness Detail -->
				<view v-show="control_target === 'color'" class="color-detail">
					<view class="brightness">
						<icon :class="dayIconClass" />
						<slider class="bright-slider" :value="sliderValues.bright" min="0" max="100"
							:active-color="colorStyle.slider_bg" @change="brightSliderChange" />
						<icon :class="dayIconClass" />
					</view>
					<view class="color-select">
						<view v-for="theme in colorThemes" :key="theme.styleNum"
							:class="['color-item', theme.className]"
							:style="{ border: colorStyle.styleNum === theme.styleNum ? '1px solid #b0d5b4' : 'none' }"
							@tap="switchColorStyle(theme.styleNum)">
							<icon v-if="theme.styleNum === 4" class="yueliang-icon"></icon>
						</view>
					</view>
				</view>

				<!-- Font Detail -->
				<view v-if="control_target === 'font'"  class="font-detail">
					<view class="fontSize">
						<icon :class="[fontIconClass, 'small']"></icon>
						<slider class="font-slider" :value="sliderValues.font" min="24" max="40" step="2"
							:active-color="colorStyle.slider_bg" @change="fontSliderChange" />
						<icon :class="fontIconClass"></icon>
					</view>
					<view class="fontSelector" @tap="isShowFontSelector = true">
						<text>{{ currentFontFamily }}</text>
					</view>
					<view class="fontDetail" :style="{ backgroundColor: colorStyle.control_bg }"
						:hidden="!isShowFontSelector">
						<view class="font-title">
							<icon :class="leftFontIconClass" @tap="isShowFontSelector = false"></icon>
							<text :style="secondaryTextStyle">选择字体</text>
						</view>
						<view class="allItem">
							<view v-for="font in allFontFamily" :key="font" class="font-item" @tap="changeFontFamily(font)">
								<text class="fontName"
									:style="{ color: currentFontFamily === font ? colorStyle.control_fontColor : secondaryTextStyle.color }">
									{{ font }}
								</text>
								<icon v-if="currentFontFamily === font" type="success_no_circle" size="12"/>
							</view>
						</view>
					</view>
				</view>

				<!-- 
				  !!! ATTENTION !!!
				  The Table of Contents Panel has been MOVED from here...
				-->
			</view>
			
			<!-- 
			  !!! ATTENTION !!!
			  ... to here. It is now a top-level element inside .reader-container,
			  so its visibility is independent of the other control panels.
			-->
			<view :class="['mulu-detail', { active: isShowMulu }]" @tap.self="closeMulu">
				<view class="muluPanel" :style="{ backgroundColor: colorStyle.content_bg }">
					<swiper class="mulu-swipers" :current="muluSwiperNum" @change="muluSwiperChange">
						<swiper-item class="mulu-swiper">
							<view class="search">
								<icon type="search" size="15" color="#ccc" />
								<input type="text" placeholder="搜索全书内容" placeholder-class="muluPl" @confirm="doContentSearch" />
							</view>
							<view class="bookInfo" :style="{ color: secondaryTextStyle.color }">
								<image :src="headImg" mode="scaleToFill"></image>
								<view class="book">
									<text class="factionName">{{ factionName }}</text>
									<text class="author">{{ book.author }}</text>
								</view>
								<view class="readerTime">
									<text><text class="num">{{ hours }}</text>时<text class="num">{{ minutes }}</text>分</text>
									<text class="timeDes">读书时长</text>
								</view>
							</view>
							<scroll-view class="sections" scroll-y="true">
								<view v-for="item in allSectionData" :key="item.sectionId" class="section"
									:style="{ color: item.sectionNum === sliderValues.section ? colorStyle.control_fontColor : secondaryTextStyle.color }"
									@tap="loadChapterBySelection(item)">
									<text class="factionNum">第{{ item.sectionNum }}章</text>
									<text class="factionName">{{ item.sectionTitle }}</text>
								</view>
							</scroll-view>
						</swiper-item>
						<swiper-item class="shuqian-swiper">书签</swiper-item>
						<swiper-item class="huaxian-swiper">划线</swiper-item>
					</swiper>
					<view class="bottomCtr">
						<text :style="{ color: getMuluTabStyle(0) }">目录</text>
						<text :style="{ color: getMuluTabStyle(1) }">书签</text>
						<text :style="{ color: getMuluTabStyle(2) }">划线</text>
					</view>
				</view>
			</view>

		</view>
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
				control_all:        false,
				control_tab:        false,
				control_detail:     false,
				control_target:     '',      
				// Book and Content State
				book: {},
				bookid: '',
				content: '',
				headImg: '',
				factionName: '',
				allSectionData: [],
				sectionNum: 1,
				maxChapterNum: 0,
				loaded: false,

				// Reading Progress State
				pageIndex: 1,
				maxPageNum: 1,
				leftValue: 0,
				
				// Reading Timer State
				readingTime: 0,
				hours: 0,
				minutes: 0,
				startReadingToken: "",

				// UI & Interaction State
				windows: {
					height: 0,
					width: 0,
					pxHeight: 0,
					pxWidth: 0,
					pixelRatio: 1
				},
				touchState: {
					lastX: 0,
					lastY: 0,
					hasMoved: false,
					direction: 0, // 0 for left, 1 for right
				},
				isShowMulu: false,
				muluSwiperNum: 0,
				isShowFontSelector: false,

				// User Settings
				sliderValues: {
					section: 1,
					bright: 80,
					font: 40 // rpx
				},
				isUseBrightModel: false,
				currentFontFamily: '等线',
				allFontFamily: ['微软雅黑', '黑体', 'Arial', '楷体', '等线'],
				lineHeight: 50, // rpx

				colorStyle: {
					content_bg: '#f5f9fc',
					styleNum: 1,
					slider_bg: '#fd9941',
					control_bg: '#ffffff',
					control_fontColor: '#fd9941'
				},
				colorThemes: [
					{ styleNum: 1, className: 'white-bg', config: { content_bg: '#f5f9fc', styleNum: 1, slider_bg: '#fd9941', control_bg: '#ffffff', control_fontColor: '#fd9941' } },
					{ styleNum: 2, className: 'pink-bg', config: { content_bg: '#f5f0da', styleNum: 2, slider_bg: '#a6832f', control_bg: '#f8f3e0', control_fontColor: '#a6832f' } },
					{ styleNum: 3, className: 'green-bg', config: { content_bg: '#c0edc6', styleNum: 3, slider_bg: '#359112', control_bg: '#ccf1d0', control_fontColor: '#359112' } },
					{ styleNum: 4, className: 'back-bg', config: { content_bg: '#1a1e21', styleNum: 4, slider_bg: '#bb7333', control_bg: '#101417', control_fontColor: '#bb7333' } },
				],
			}
		},
		computed: {
			contentStyle() {
				return {
					'width': `${this.maxPageNum * 100}%`,
					'-webkit-column-count': this.maxPageNum,
					'column-count': this.maxPageNum,
					'left': `${this.leftValue}rpx`,
					'background-color': this.isUseBrightModel ? 'transparent' : this.colorStyle.content_bg,
					'font-family': this.currentFontFamily,
					'font-size': `${this.sliderValues.font}rpx`,
				};
			},
			contentInnerStyle() {
				return {
					'line-height': `${this.lineHeight}rpx`,
					// Adjust text color for night mode
					'color': this.colorStyle.styleNum === 4 ? '#85898c' : '#000800',
				};
			},
			coverStyle() {
				return {
					'background-color': `rgba(0, 0, 0, ${1 - this.sliderValues.bright / 100})`,
				};
			},
			controlPanelStyle() {
				return {
					'background-color': this.colorStyle.control_bg,
				};
			},
			secondaryTextStyle() {
				// For text elements that need to adapt to the theme (e.g., in mulu)
				return {
					'color': this.colorStyle.styleNum === 4 ? '#63676a' : '#655f4f',
				}
			},
			muluIconClass() {
				return `iconfont mymulu${this.colorStyle.styleNum}-icon`;
			},
			jingduIconClass() {
				const isSelected = this.control_target === 'jingdu';
				const selectSuffix = isSelected ? '-select' : '';
				return `iconfont myjingdu${this.colorStyle.styleNum}${selectSuffix}-icon`;
			},
			dayIconClass() {
				const isSelected = this.control_target === 'color';
				const selectSuffix = isSelected ? '-select' : '';
				return `iconfont myday${this.colorStyle.styleNum}${selectSuffix}-icon`;
			},
			fontIconClass() {
				return `iconfont myfont${this.colorStyle.styleNum}-icon`;
			},
			rLeftIconClass() {
				return `rleft${this.colorStyle.styleNum}-icon`;
			},
			rRightIconClass() {
				return `rright${this.colorStyle.styleNum}-icon`;
			},
			leftFontIconClass() {
				return `leftFont${this.colorStyle.styleNum}-icon`;
			}
		},
		async onLoad(options) {
			this.initWindowInfo();
			this.bookid = options.bookid || '58cbc7c8618cec336c6e8a10'; // Default book for testing
			this.factionName = options.name || '超品战兵';
			wx.setNavigationBarTitle({ title: this.factionName });
			
			this.loadUserSettings();
			
			await this.fetchBookDetails();
			await this.fetchInitialContent();
		},
		async onShow() {
			await this.startReadingTimer();
			await this.fetchReadingTime();
		},
		onHide() {
			this.saveUserSettings();
			this.endReadingTimer();
		},
		onUnload() {
			this.saveUserSettings();
			this.endReadingTimer();
		},
		methods: {
			// --- Initialization and Data Fetching ---
			initWindowInfo() {
				const info = uni.getWindowInfo();
				const rpxRatio = 750 / info.windowWidth;
				this.windows = {
					pxWidth: info.screenWidth,
					pxHeight: info.screenHeight,
					width: info.windowWidth * rpxRatio,
					height: info.windowHeight * rpxRatio,
					pixelRatio: info.pixelRatio
				};
			},
			async fetchBookDetails() {
				try {
					const res = await uni.request({ url: getBookDetail(this.bookid) });
					if (res[1].data.code === 200) {
						this.book = res[1].data.data;
						const chapterRes = await uni.request({ url: getmaxChapterNum(this.bookid) });
						this.maxChapterNum = chapterRes[1].data.data;
					}
				} catch (e) {
					console.error("fetchBookDetails failed", e);
				}
			},
			async fetchInitialContent() {
				wx.showLoading({ title: '加载中...', mask: true });
				try {
					const token = uni.getStorageSync('token');
					const res = await uni.request({ url: getContentByHistory(token, this.bookid) });
					if (res[1].data.code === 200) {
						const { content, num } = res[1].data.data;
						this.updateContent(content, num);
					} else {
						throw new Error(res[1].data.message);
					}
				} catch (e) {
					console.error("fetchInitialContent failed", e);
					Util.showErrMsg(this, '获取章节内容失败');
				} finally {
					wx.hideLoading();
				}
			},
			
			// --- Content Management ---
			updateContent(newContent, newSectionNum) {
				this.content = newContent;
				this.sectionNum = newSectionNum;
				this.sliderValues.section = newSectionNum;
				
				this.pageIndex = 1;
				this.leftValue = 0;
				
				this.recalculatePages();

				setTimeout(() => { this.loaded = true; }, 300);
			},
			recalculatePages() {
				this.maxPageNum = this.countPageNum(this.content);
				// After recalculating, ensure current page is valid
				if (this.pageIndex > this.maxPageNum) {
					this.pageIndex = this.maxPageNum;
				}
				this.leftValue = -this.windows.width * (this.pageIndex - 1);
			},
			countPageNum(str) {
				if (!str) return 1;
				
				const viewWidthRpx = this.windows.width;
				const viewHeightRpx = this.windows.height - 40; // Approx margin top/bottom
				const contentWidthRpx = viewWidthRpx - 70; // padding left/right

				const charsPerLine = Math.floor(contentWidthRpx / this.sliderValues.font);
				
				let totalHeight = 0;
				const paragraphs = str.split(/\n+/);
				paragraphs.forEach(p => {
					let linesInPara = 1;
					if (p.length > 0) {
						linesInPara = Math.ceil(p.length / charsPerLine);
					}
					totalHeight += linesInPara * this.lineHeight;
				});
				
				const pageCount = Math.ceil(totalHeight / viewHeightRpx);
				return pageCount > 0 ? pageCount : 1;
			},

			// --- Chapter Navigation ---
			async nextChapter() {
				if (this.sectionNum >= this.maxChapterNum) {
					uni.showToast({ title: '已经是最后一章了', icon: 'none' });
					return;
				}
				await this.loadChapterByNumber(this.sectionNum + 1);
			},
			async prevChapter() {
				if (this.sectionNum <= 1) {
					uni.showToast({ title: '已经是第一章了', icon: 'none' });
					return;
				}
				await this.loadChapterByNumber(this.sectionNum - 1);
			},
			async loadChapterByNumber(sectionNum) {
				wx.showLoading({ title: '加载中...', mask: true });
				try {
					const res = await uni.request({ url: getChapterByNumAndBookId(this.bookid, sectionNum) });
					if(res[1].data.code === 200) {
						this.updateContent(res[1].data.data.content, sectionNum);
					} else {
						throw new Error(res[1].data.message);
					}
				} catch (e) {
					console.error("loadChapterByNumber failed", e);
					Util.showErrMsg(this, '获取章节内容失败');
				} finally {
					wx.hideLoading();
				}
			},
			async loadChapterBySelection(section) {
				wx.showLoading({ title: '加载中...', mask: true });
				try {
					const token = uni.getStorageSync('token');
					const res = await uni.request({ url: getContentById(section.sectionId, token, this.bookid) });
					if(res[1].data.code === 200) {
						this.updateContent(res[1].data.data.content, section.sectionNum);
						this.closeMulu();
					} else {
						throw new Error(res[1].data.message);
					}
				} catch (e) {
					console.error("loadChapterBySelection failed", e);
					Util.showErrMsg(this, '获取章节内容失败');
				} finally {
					wx.hideLoading();
				}
			},
			
			// --- Touch Events for Paging ---
			handleTouchStart(e) {
				this.touchState.lastX = e.touches[0].pageX;
				this.touchState.lastY = e.touches[0].pageY;
				this.touchState.hasMoved = false;
			},
			handleTouchMove(e) {
				const currentX = e.touches[0].pageX;
				const deltaX = currentX - this.touchState.lastX;
				
				if (Math.abs(deltaX) > 10) { // Threshold to detect swipe
					this.touchState.hasMoved = true;
					this.touchState.direction = deltaX > 0 ? 1 : 0; // 1: right, 0: left
				}
			},
			async handleTouchEnd() {
				if (!this.touchState.hasMoved) {
					this.toggleControlPanel();
					return;
				}
				
				if (this.touchState.direction === 0) { // Swipe Left (Next Page)
					if (this.pageIndex < this.maxPageNum) {
						this.pageIndex++;
					} else {
						await this.nextChapter();
						return;
					}
				} else { // Swipe Right (Prev Page)
					if (this.pageIndex > 1) {
						this.pageIndex--;
					} else {
						const res = await uni.showModal({ title: '提示', content: '已经是本章第一页，要翻到上一章吗？' });
						if (res.confirm) {
							await this.prevChapter();
						}
						return;
					}
				}
				this.leftValue = -this.windows.width * (this.pageIndex - 1);
			},
			
			// --- Control Panel Logic ---
			toggleControlPanel() {
				const { lastX, lastY } = this.touchState;
				const { pxWidth, pxHeight } = this.windows;
				
				if (lastX > pxWidth * 0.25 && lastX < pxWidth * 0.75 && lastY > pxHeight * 0.25 && lastY < pxHeight * 0.75) {
					if (!this.control_all) {
						this.control = { all: true, control_tab: true, control_detail: false, target: '' };
					} else {
						this.control_all = false;
						this.isShowFontSelector = false;
					}
				}
			},
			// 精简后的 showControlDetail
			showControlDetail (target){
				console.log("showControlDetail invoked")
				console.log("control_target is ",this.control_target)
				console.log("control_detail is ",this.control_detail)
				this.control_all = true
			  // 已经是当前面板 → 关闭
			  if (this.control_target === target && this.control_detail){
			    this.control_detail = false
			    this.control_tab    = true
			    this.control_target = ''
			    return
			  }
			  // 打开对应面板
			  this.control_detail = true
			  this.control_tab    = false
			  this.control_target = target
			},


			
			// --- Settings Handlers ---
			sectionSliderChange(e) {
				this.loadChapterByNumber(e.detail.value);
			},
			brightSliderChange(e) {
				this.sliderValues.bright = e.detail.value;
				if (!this.isUseBrightModel) {
					wx.showModal({
						title: '提示',
						content: '使用亮度调节将覆盖颜色主题，建议使用系统亮度。是否继续？',
						success: (res) => {
							if (res.confirm) {
								this.isUseBrightModel = true;
								this.switchColorStyle(1);
							} else {
								// Revert slider if user cancels
								this.sliderValues.bright = 80;

							}
						}
					});
				}
			},
			fontSliderChange(e) {
				this.sliderValues.font = e.detail.value;
				this.recalculatePages();
			},
			switchColorStyle(styleNum) {
				const theme = this.colorThemes.find(t => t.styleNum === styleNum);
				if(theme) {
					this.colorStyle = theme.config;
				}
				this.isUseBrightModel = false;
			},
			changeFontFamily(font) {
				this.currentFontFamily = font;
				this.isShowFontSelector = false;
				this.recalculatePages();
			},

			// --- Mulu (Table of Contents) Logic ---
			async openMulu() {
				console.log('[openMulu] tapped');

				this.control_all = false;
				try {
					const res = await uni.request({ url: getMulu(this.bookid, this.sectionNum) });
					if (res[1].data.code === 200) {
						console.log('[openMulu] res[1].data:', res[1].data);
						this.allSectionData = res[1].data.data.sectionArray;
						this.headImg = res[1].data.data.headImg;
						this.isShowMulu = true;
					}
				} catch(e) {
					console.error("openMulu failed", e);
					uni.showToast({ title: '获取目录失败', icon: 'none' });
				}
			},
			closeMulu() {
				this.isShowMulu = false;
			},
			muluSwiperChange(e) {
				this.muluSwiperNum = e.detail.current;
			},
			getMuluTabStyle(index) {
				const isActive = this.muluSwiperNum === index;
				return isActive ? this.colorStyle.control_fontColor : this.secondaryTextStyle.color;
			},
			doContentSearch(e) {
				console.log("Search term:", e.detail.value);
				uni.showToast({ title: '搜索功能待实现', icon: 'none' });
			},

			// --- User Settings Persistence ---
			saveUserSettings() {
				try {
					const settings = {
						sliderValues: this.sliderValues,
						currentFontFamily: this.currentFontFamily,
						colorStyle: this.colorStyle,
						pageIndex: this.pageIndex,
					};
					uni.setStorageSync('reader_setting', JSON.stringify(settings));
				} catch(e) {
					console.error('Failed to save user settings', e);
				}
			},
			loadUserSettings() {
				try {
					const settingsStr = uni.getStorageSync('reader_setting');
					if (settingsStr) {
						const settings = JSON.parse(settingsStr);
						this.sliderValues = settings.sliderValues || this.sliderValues;
						this.currentFontFamily = settings.currentFontFamily || this.currentFontFamily;
						this.colorStyle = settings.colorStyle || this.colorStyle;
						this.pageIndex = settings.pageIndex || this.pageIndex;
						
						this.leftValue = -this.windows.width * (this.pageIndex - 1);
					}
				} catch(e) {
					console.error('Failed to load user settings', e);
				}
			},

			// --- Reading Time Tracking ---
			async startReadingTimer() {
				try {
					const token = uni.getStorageSync('token');
					const res = await uni.request({ url: GetStartReading(token, this.bookid) });
					if (res[1].data.code === 200) {
						this.startReadingToken = res[1].data.data;
					}
				} catch(e) {
					console.error("startReadingTimer failed", e);
				}
			},
			async endReadingTimer() {
				if (this.startReadingToken) {
					try {
						await uni.request({ url: GetendReading(this.startReadingToken) });
						this.startReadingToken = "";
					} catch(e) {
						console.error("endReadingTimer failed", e);
					}
				}
			},
			async fetchReadingTime() {
				try {
					const token = uni.getStorageSync("token");
					const res = await uni.request({ url: getOneBookReadingTime(token, this.bookid) });
					if (res[1].data.code === 200) {
						this.hours = res[1].data.data.hours;
						this.minutes = res[1].data.data.minutes;
					}
				} catch(e) {
					console.error("fetchReadingTime failed", e);
				}
			},
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

		background-color: #fff;
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
		z-index:15;            
	}




	.reader-container .control-detail>view {
		width: 100%;
		position: absolute;
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
		z-index: 5;
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
		z-index: 20; /* <-- 添加这一行，确保它在最上层 */

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

	.mulu-detail .bookInfo {
	    display: flex;
	    flex-direction: row;
	    align-items: center;
	    margin-left: 30rpx;
	    font-size: 28rpx;
	    position: relative;
	    padding: 20rpx 0;
	}
	
	.mulu-detail .bookInfo image {
	    height: 80rpx;
	    width: 50rpx;
	    border-radius: 8rpx;
	    flex-shrink: 0;
	}
	
	.mulu-detail .bookInfo .book {
	    margin-left: 20rpx;
	    display: flex;
	    flex-direction: column;
	    justify-content: center;
	}
	
	.mulu-detail .bookInfo .factionName {
	    overflow: hidden;
	    text-overflow: ellipsis;
	    white-space: nowrap;
	    max-width: 300rpx;
	    display: block;
	}

	
	.mulu-detail .bookInfo .author {
	    font-size: 24rpx;
	    color: #888;
	}
	
	.mulu-detail .bookInfo .readerTime {
	    position: absolute;
	    right: 30rpx;
	    top: 20rpx;
	    text-align: right;
	    font-size: 24rpx;
	}
	
	.mulu-detail .bookInfo .readerTime .num {
	    font-size: 28rpx;
	    font-weight: bold;
	    color: #ff9900;
	}
	.mulu-detail .bookInfo .readerTime .timeDes {
	    display: block;
	    font-size: 20rpx;
	    color: #888;
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
		from {
			width: 0;
		}

		to {
			width: 86%;
		}
	}
</style>

<style>
	/* All original CSS from template.txt can be copied here. */
	/* The provided CSS is extensive, so for brevity, it's omitted. */
	/* It is important to copy the original <style> block to this new file for it to work. */
	
	/* --- Key CSS Selectors from the original file for reference --- */
	.reader-container {
		height: 100vh;
		width: 100vw;
		overflow: hidden;
		position: fixed;
	}

	.reader-container .content {
		display: block;
		overflow-x: hidden;
		height: 100%;
		box-sizing: border-box;
		column-gap: 40rpx;
		padding: 20rpx 30rpx 0 40rpx;
		position: relative;
		left: 0; /* Default position */
		will-change: left; /* Performance hint for browser */
	}

	.reader-container .content.loaded {
		transition: left 0.3s ease-in-out;
	}

	.reader-container .control {
		width: 100%;
		position: fixed;
		height: 110rpx;
		bottom: 0;
		left: 0;
		display: flex;
		flex-flow: row nowrap;
		justify-content: space-around;
		border-top: 1px solid #d7d7d7;
		z-index: 10;
	}

	.reader-container .control-item {
		height: 100rpx;
		margin-top: 5rpx;
		width: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.reader-container .control-item icon {
		font-size: 50rpx;
	}

	.reader-container .control-detail {
		min-height: 200rpx;
		width: 100%;
		position: absolute;
		bottom: 110rpx;
		left: 0;
		border-bottom: 1px solid #d7d7d7;
		z-index: 30;
	}
	
	.reader-container .cover {
		position: absolute;
		height: 100%;
		width: 100%;
		top: 0;
		left: 0;
		z-index: 5; /* Above content, below controls */
		pointer-events: none; /* Allows clicks to pass through */
	}
	
	/* Styles for mulu (TOC) Panel */
	.mulu-detail {
		position: absolute;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.5);
		width: 100%;
		top: 0;
		left: 0;
		display: none;
		z-index: 20;
	}

	.mulu-detail.active {
		display: block;
	}

	.mulu-detail .muluPanel {
		width: 0%;
		height: 100%;
		background-color: white; /* Default, will be overridden by style binding */
	}

	.mulu-detail.active .muluPanel {
		animation: slideShow 0.4s ease-out forwards;
	}

	.mulu-detail .mulu-swipers {
		height: 92%;
		width: 100%;
	}
	
	.mulu-detail .sections {
		margin-top: 20rpx;
		/* Use vh to make it responsive to screen height */
		height: calc(100vh - 400rpx); /* Approximate height minus header/footer */
	}
	
	.mulu-detail .mulu-swiper .section {
		height: 80rpx;
		line-height: 80rpx;
		border-bottom: 1rpx solid #dedede;
		font-size: 28rpx;
		padding-left: 30rpx;
		box-sizing: border-box;
	}
	
	/* ... rest of the original CSS ... */

	/* Icon font definitions are crucial, assuming they are set up globally or in App.vue */
	@font-face {
	  font-family: 'iconfont';
	  /* ... src urls ... */
	}
	
	.iconfont {
	  font-family: "iconfont" !important;
	  font-size: 16px;
	  font-style: normal;
	  -webkit-font-smoothing: antialiased;
	  -moz-osx-font-smoothing: grayscale;
	}

	/* ... icon content definitions e.g., .mymulu1-icon::before { content: '\e60b;'; } ... */


.reader-container .control,
.reader-container .control-detail {
  background-color: #ffffff; /* 兜底，夜间模式会被 inline 样式覆盖 */
  z-index: 30;               /* 适当抬高，确保压住正文 */
}
</style>

