package com.curso.filtro;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class ServletInputStreamImpl extends ServletInputStream {

    private InputStream is;

    public ServletInputStreamImpl(InputStream is) {
        this.is = is;
    }

    public int read() throws IOException {
        return is.read();
    }

    public boolean markSupported() {
        return false;
    }

    public synchronized void mark(int i) {
        throw new RuntimeException(new IOException("mark/reset not supported"));
    }

    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		// TODO Auto-generated method stub
		
	}
}