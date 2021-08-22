import React, { Component } from "react";
import "./App.css";
import GraphContainer from "./Components/GraphContainer/GraphContainer";
import InputForm from "./Components/InputForm/InputForm";
import axios from "axios";

class App extends Component {
  state = {
    selectedFile: "",
    reqResp: false,
  };

  shouldComponentUpdate(nextProps, nextState) {
    if (this.state.reqResp !== nextState.reqResp) return true;
  }

  handleInputChange = (event) => {
    this.setState({
      selectedFile: event.target.files[0],
    });
  };

  handleSubmit = () => {
    const data = new FormData();
    data.append("file_input", this.state.selectedFile);
    console.warn(this.state.selectedFile);
    let url = "http://localhost:9080/kgsum";

    axios
      .post(url, data, {
        // receive two parameter endpoint url ,form data
      })
      .then((res) => {
        // then print response status
        if (res.data) {
          this.setState({
            reqResp: res.data.status,
          });
        }
      });
  };

  render() {
    var resp = this.state.reqResp ? (
      <div>
        <GraphContainer fileLoaded={this.state.reqResp} />
      </div>
    ) : (
      <InputForm
        onSubmit={this.handleSubmit}
        onFileInputChange={this.handleInputChange}
      />
    );
    return resp;
  }
}

export default App;
