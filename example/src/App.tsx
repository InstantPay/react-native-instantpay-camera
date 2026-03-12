import { useState } from 'react';
import { View, StyleSheet, Button, Modal, Text, Pressable, Alert, ScrollView, Dimensions } from 'react-native';
import { InstantpayCameraView } from 'react-native-instantpay-camera';

const windowDimensions = Dimensions.get('window');
const screenDimensions = Dimensions.get('screen');

export default function App() {

	const [isCameraOn, setIsCameraOn] = useState(false);
	const [modalVisible, setModalVisible] = useState(false);

	const onCloseCallbackHandeler = (event:any) => {
		console.log('onCloseCallbackHandeler',event.nativeEvent)
		setModalVisible(false)
	}

	const onErrorCallbackHandeler = (event:any) => {
		console.log('onErrorCallbackHandeler',event.nativeEvent)
	}

	const onSuccessCallbackHandeler = (event:any) => {
		console.log('onSuccessCallbackHandeler',event.nativeEvent)
	}

	const onCameraStartedCallbackHandeler = (event:any) => {
		console.log('onCameraStartedCallbackHandeler',event.nativeEvent)
	}

	return (
		<View style={styles.container}>
			<Modal
				animationType="slide"
				transparent={false}
				visible={modalVisible}
				onRequestClose={() => {
					Alert.alert('Modal has been closed.');
					setModalVisible(!modalVisible);
				}}
				style={{borderWidth:5,borderColor:'green'}}
			>
					<ScrollView style={styles.scrollStyle}>
						<View style={styles.centeredView}>
							<InstantpayCameraView 
								style={styles.box} 
								color="#32a852"
								photoCaptureConfig={{
									quality: "HIGH",
									flash: "AUTO",
									saveToGallery: true
								}}
								onCloseCallback={(event) => onCloseCallbackHandeler(event)}
								onErrorCallback={(event) => onErrorCallbackHandeler(event)}
								onSuccessCallback={(event) => onSuccessCallbackHandeler(event)}
								onCameraStartedCallback={(event) => onCameraStartedCallbackHandeler(event)}
							/>
						</View>
						<Pressable style={[styles.button, styles.buttonClose]}
							onPress={() => setModalVisible(!modalVisible)}>
							<Text style={styles.textStyle}>Hide Modal</Text>
						</Pressable>
					</ScrollView>
			</Modal>
			<Button
				onPress={() => setModalVisible(true)}
				title='Open Camera'
			/>
		</View>
	);
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		alignItems: 'center',
		justifyContent: 'center',
	},
	box: {
		width: '100%',
		height: '100%',
		//padding:110,
		borderWidth:2,borderColor:'red',
	},
	scrollStyle:{
		flex:1
	},
	centeredView: {
		height: screenDimensions.height / 1.2,
		//borderWidth:2,borderColor:'red',
	},
	button: {
		margin:10,
		marginTop:10,
		borderRadius: 20,
		padding: 10,
		elevation: 2,
	},
	buttonOpen: {
		backgroundColor: '#F194FF',
	},
	buttonClose: {
		backgroundColor: '#2196F3',
	},
	textStyle: {
		color: 'white',
		fontWeight: 'bold',
		textAlign: 'center',
	},
	modalText: {
		marginBottom: 15,
		textAlign: 'center',
	},
});
