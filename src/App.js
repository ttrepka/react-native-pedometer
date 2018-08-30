// @flow
import React from 'react';
import { Button, DeviceEventEmitter, StyleSheet, Text, View } from 'react-native';

import Pedometer from './Pedometer';

export default class App extends React.PureComponent<{}> {
  state = {
    counting: false,
    stepCount: 0,
    stepDetected: 0,
  };

  componentDidMount() {
    DeviceEventEmitter.addListener('stepCount', this.addStepCount);
    DeviceEventEmitter.addListener('stepDetected', this.addStepDetected);
  }

  componentWillUnmount() {
    DeviceEventEmitter.removeListener('stepCount', this.addStepCount);
    DeviceEventEmitter.removeListener('stepDetected', this.addStepDetected);
  }

  addStepCount = event => {
    this.setState({ stepCount: event.takenSteps });
  };

  addStepDetected = () => {
    this.setState(prevState => ({ stepDetected: prevState.stepDetected + 1 }));
  };

  toggleCounting = () => {
    this.setState(
      prevState => ({ counting: !prevState.counting }),
      () => {
        const { counting } = this.state;
        if (counting) {
          Pedometer.start();
        } else {
          Pedometer.stop();
        }
      },
    );
  };

  render() {
    const { counting, stepCount, stepDetected } = this.state;

    return (
      <View style={styles.container}>
        <View style={styles.button}>
          <Button onPress={this.toggleCounting} title={counting ? 'Stop' : 'Start'} />
        </View>

        <Text style={styles.steps}>
          Step count:
          <Text style={styles.count}>{stepCount}</Text>
        </Text>
        <Text style={styles.steps}>
          Step detected events:
          <Text style={styles.count}>{stepDetected}</Text>
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },

  button: {
    marginBottom: 20,
  },

  steps: {
    fontSize: 18,
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },

  count: {
    fontWeight: 'bold',
  },
});
