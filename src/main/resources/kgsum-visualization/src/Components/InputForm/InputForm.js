import React, { Component } from "react";
import Grid from "@material-ui/core/Grid";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import Button from "@material-ui/core/Button";
import Container from "@material-ui/core/Container";
import "./inputForm.scss";

class InputForm extends Component {
  render() {
    return (
      <div className="input-form">
        <Container maxWidth="lg">
          <h1 className="page-title">
            HAREJava - A Knowledge Graph Summarization Algorithm
          </h1>
          <Grid container spacing={12}>
            <Grid item lg={12} className="input-file">
              <label className="text-white">
                Please upload TTL file to summarize
              </label>
              <input
                type="file"
                className="form-control"
                name="upload_file"
                accept=".ttl"
                onChange={this.props.onFileInputChange}
              />
              <Button
                type="submit"
                className="btn btn-dark"
                onClick={this.props.onSubmit}
                variant="contained"
                startIcon={<CloudUploadIcon />}
              >
                Upload
              </Button>
            </Grid>
          </Grid>
        </Container>
      </div>
    );
  }
}

export default InputForm;
